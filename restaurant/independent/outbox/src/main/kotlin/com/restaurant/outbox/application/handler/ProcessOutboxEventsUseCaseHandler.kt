package com.restaurant.outbox.application.handler

import com.restaurant.outbox.application.dto.OutboxMessageRepository
import com.restaurant.outbox.application.dto.model.OutboxMessage
import com.restaurant.outbox.application.dto.model.OutboxMessageStatus
import com.restaurant.outbox.application.usecase.ProcessOutboxEventsUseCase
import com.restaurant.outbox.infrastructure.exception.OutboxException
import com.restaurant.outbox.infrastructure.messaging.OutboxMessageSender
import org.slf4j.LoggerFactory
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

/**
 * Outbox 이벤트 처리 Use Case 구현체
 * Rule 80: 독립 모듈의 Application Layer Use Case 구현체
 */
@Service
open class ProcessOutboxEventsUseCaseHandler(
    private val outboxMessageRepository: OutboxMessageRepository,
    private val outboxMessageSender: OutboxMessageSender,
) : ProcessOutboxEventsUseCase {
    private val logger = LoggerFactory.getLogger(this::class.java)

    @Transactional
    override fun process(outboxMessage: OutboxMessage) {
        try {
            outboxMessageSender.processAndSendMessage(outboxMessage)
            outboxMessage.id?.let { id ->
                outboxMessageRepository.updateStatus(id, OutboxMessageStatus.SENT)
                logger.debug("Successfully processed outbox message with ID: {}", id)
            }
        } catch (e: OutboxException.KafkaSendFailedException) {
            logger.error("Kafka send failed for message: {}, Error: {}", outboxMessage.id, e.message)
            outboxMessage.id?.let { id ->
                outboxMessageRepository.updateStatus(id, OutboxMessageStatus.FAILED, true)
            }
            throw e
        } catch (e: Exception) {
            logger.error("Unexpected error processing message: {}, Error: {}", outboxMessage.id, e.message, e)
            outboxMessage.id?.let { id ->
                outboxMessageRepository.updateStatus(id, OutboxMessageStatus.FAILED, true)
            }
            throw OutboxException.MessageProcessingFailedException(
                "Failed to process outbox message with ID ${outboxMessage.id}",
                e,
            )
        }
    }

    @Transactional
    override fun processPendingMessages(batchSize: Int): Int {
        try {
            val pendingMessages =
                outboxMessageRepository.findAndMarkForProcessing(
                    OutboxMessageStatus.PENDING,
                    batchSize,
                )

            if (pendingMessages.isEmpty()) {
                return 0
            }

            logger.debug("Found {} pending messages to process", pendingMessages.size)

            pendingMessages.forEach { message ->
                try {
                    process(message)
                } catch (e: Exception) {
                    logger.error("Failed to process message ID: {}", message.id, e)
                    // 개별 메시지 실패는 전체 배치를 중단시키지 않음
                }
            }

            return pendingMessages.size
        } catch (e: Exception) {
            logger.error("Error in processing pending messages", e)
            throw OutboxException.MessageProcessingFailedException(
                "Failed to process pending messages",
                e,
            )
        }
    }

    @Transactional
    override fun retryFailedMessages(
        maxRetries: Int,
        batchSize: Int,
    ): Int {
        try {
            val failedMessages =
                outboxMessageRepository.findByStatusAndRetryCountLessThan(
                    status = OutboxMessageStatus.FAILED,
                    maxRetries = maxRetries,
                    limit = batchSize,
                )

            if (failedMessages.isEmpty()) {
                return 0
            }

            logger.debug("Found {} failed messages for retry", failedMessages.size)

            var retryCount = 0
            failedMessages.forEach { message ->
                try {
                    message.id?.let { id ->
                        outboxMessageRepository.updateStatus(id, OutboxMessageStatus.PENDING, false)
                        retryCount++
                        logger.info("Message ID {} marked as PENDING for retry", id)
                    }
                } catch (e: Exception) {
                    logger.error("Error resetting failed message to PENDING: {}", message.id, e)
                }
            }

            return retryCount
        } catch (e: Exception) {
            logger.error("Error in failed message retry process", e)
            throw OutboxException.MessageProcessingFailedException(
                "Failed to retry failed messages",
                e,
            )
        }
    }
}
