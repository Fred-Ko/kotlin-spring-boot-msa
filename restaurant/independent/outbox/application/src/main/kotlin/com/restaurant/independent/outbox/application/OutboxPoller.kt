package com.restaurant.independent.outbox.application

import com.restaurant.independent.outbox.application.error.OutboxException
import com.restaurant.independent.outbox.application.port.OutboxMessageRepository
import com.restaurant.independent.outbox.application.port.OutboxMessageSenderPort
import com.restaurant.independent.outbox.application.port.model.OutboxMessage
import com.restaurant.independent.outbox.application.port.model.OutboxMessageStatus
import org.slf4j.LoggerFactory
import org.springframework.beans.factory.annotation.Value
import org.springframework.scheduling.annotation.Scheduled
import org.springframework.stereotype.Component
import org.springframework.transaction.annotation.Transactional

/**
 * Outbox 메시지를 주기적으로 폴링하여 처리하는 컴포넌트.
 */
@Component
class OutboxPoller(
    private val outboxMessageRepository: OutboxMessageRepository,
    private val outboxMessageSender: OutboxMessageSenderPort,
    @Value("\${outbox.polling.max-retries:3}")
    private val maxRetries: Int = 3,
    @Value("\${outbox.polling.batch-size:100}")
    private val batchSize: Int = 100,
) {
    private val log = LoggerFactory.getLogger(javaClass)

    /**
     * 대기 중인 메시지를 처리합니다.
     * FOR UPDATE SKIP LOCKED를 사용하여 동시성을 제어합니다.
     */
    @Scheduled(fixedDelayString = "\${outbox.polling.interval:1000}")
    @Transactional
    fun pollMessages() {
        try {
            // PENDING 상태의 메시지를 조회하고 처리
            val messages = outboxMessageRepository.findByStatus(OutboxMessageStatus.PENDING, batchSize)
            messages.forEach { message ->
                try {
                    processMessage(message)
                } catch (e: Exception) {
                    handleMessageProcessingError(message, e)
                }
            }

            // 실패한 메시지 중 재시도 가능한 것들을 처리
            val failedMessages = outboxMessageRepository.findByStatus(OutboxMessageStatus.FAILED, batchSize)
            failedMessages.forEach { message ->
                try {
                    if (message.retryCount < maxRetries) {
                        processMessage(message)
                    } else {
                        // 최대 재시도 횟수를 초과한 메시지는 DEAD_LETTERED 상태로 변경
                        outboxMessageRepository.updateStatus(
                            id = message.id,
                            newStatus = OutboxMessageStatus.DEAD_LETTERED,
                        )
                        log.warn("Message ${message.id} marked as dead-lettered after ${message.retryCount} retries")
                    }
                } catch (e: Exception) {
                    handleMessageProcessingError(message, e)
                }
            }
        } catch (e: Exception) {
            log.error("Error during message polling", e)
            throw OutboxException.PollingException(
                message = "Failed to poll messages: ${e.message}",
                cause = e,
            )
        }
    }

    /**
     * 단일 메시지를 처리합니다.
     */
    private fun processMessage(message: OutboxMessage) {
        try {
            // 메시지를 PROCESSING 상태로 변경
            outboxMessageRepository.updateStatus(
                id = message.id,
                newStatus = OutboxMessageStatus.PROCESSING,
            )

            // Kafka로 메시지 전송
            outboxMessageSender.send(message)

            // 전송 성공 시 SENT 상태로 변경
            outboxMessageRepository.updateStatus(
                id = message.id,
                newStatus = OutboxMessageStatus.SENT,
            )

            log.info("Successfully processed message ${message.id}")
        } catch (e: Exception) {
            log.error("Failed to process message ${message.id}", e)
            throw OutboxException.MessageSendException(
                message = "Failed to send message to Kafka: ${e.message}",
                cause = e,
            )
        }
    }

    /**
     * 메시지 처리 오류를 처리합니다.
     */
    private fun handleMessageProcessingError(
        message: OutboxMessage,
        error: Exception,
    ) {
        try {
            // 실패 상태로 변경하고 재시도 횟수 증가
            outboxMessageRepository.updateStatus(
                id = message.id,
                newStatus = OutboxMessageStatus.FAILED,
                incrementRetry = true,
            )

            if (message.retryCount >= maxRetries) {
                log.error(
                    "Max retry count ($maxRetries) exceeded for message ${message.id}",
                    error,
                )
                throw OutboxException.MaxRetriesExceededException(
                    message = "Max retry count ($maxRetries) exceeded for message ${message.id}",
                    cause = error,
                )
            }

            log.warn(
                "Failed to process message ${message.id}, will retry later. Current retry count: ${message.retryCount}",
                error,
            )
        } catch (e: Exception) {
            log.error("Error while handling message processing failure for message ${message.id}", e)
            throw e
        }
    }
}
