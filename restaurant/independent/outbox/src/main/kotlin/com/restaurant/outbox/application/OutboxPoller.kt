package com.restaurant.outbox.application

import com.restaurant.outbox.application.port.model.OutboxMessage
import com.restaurant.outbox.application.port.model.OutboxMessageStatus
import com.restaurant.outbox.application.port.OutboxMessageRepository
import com.restaurant.outbox.infrastructure.kafka.OutboxMessageSender
import org.slf4j.LoggerFactory
import org.springframework.scheduling.annotation.Scheduled
import org.springframework.stereotype.Component
import org.springframework.transaction.annotation.Transactional
import com.restaurant.outbox.infrastructure.exception.OutboxException

@Component
class OutboxPoller(
    private val outboxMessageRepository: OutboxMessageRepository,
    private val outboxMessageSender: OutboxMessageSender,
) {
    private val logger = LoggerFactory.getLogger(this::class.java)
    private val batchSize = 100
    private val maxRetries = 3

    @Scheduled(fixedRate = 1000) // 1초마다 실행
    @Transactional
    fun pollAndProcessMessages() {
        try {
            val unprocessedMessages: List<OutboxMessage> =
                outboxMessageRepository.findAndMarkForProcessing(OutboxMessageStatus.PENDING, batchSize)

            if (unprocessedMessages.isEmpty()) {
                return
            }

            logger.debug("Found {} unprocessed messages to process", unprocessedMessages.size)

            unprocessedMessages.forEach { message ->
                try {
                    outboxMessageSender.processAndSendMessage(message)
                    message.id?.let {
                        outboxMessageRepository.updateStatus(it, OutboxMessageStatus.SENT)
                    }
                } catch (e: OutboxException.KafkaSendFailedException) {
                    logger.error(
                        "Kafka send failed for message: {}, Error: {}. Incrementing retry count.",
                        message.id,
                        e.message,
                    )
                    message.id?.let {
                        // PENDING 상태로 변경하고 재시도 횟수 증가
                        outboxMessageRepository.updateStatus(it, OutboxMessageStatus.PENDING, true)
                    }
                } catch (e: Exception) { // 그 외 예상치 못한 오류
                    logger.error(
                        "Unexpected error processing message: {}, Error: {}",
                        message.id,
                        e.message,
                        e,
                    )
                    message.id?.let {
                        // 예상치 못한 오류는 FAILED로 처리하고 재시도 횟수 증가
                        outboxMessageRepository.updateStatus(it, OutboxMessageStatus.FAILED, true)
                    }
                }
            }
        } catch (e: Exception) {
            logger.error("Error in outbox polling process", e)
        }
    }

    @Scheduled(fixedRate = 300000) // 5분마다 실행
    @Transactional
    fun retryFailedMessages() {
        try {
            // 재시도 횟수가 maxRetries 미만인 FAILED 상태의 메시지들을 조회
            val failedMessages: List<OutboxMessage> = outboxMessageRepository.findByStatusAndRetryCountLessThan(
                status = OutboxMessageStatus.FAILED,
                maxRetries = maxRetries,
                limit = batchSize
            )

            if (failedMessages.isEmpty()) {
                return
            }

            logger.debug("Found {} failed messages for retry", failedMessages.size)

            failedMessages.forEach { message: OutboxMessage ->
                try {
                    message.id?.let {
                        // 상태를 PENDING으로 변경하고, 재시도 횟수는 여기서 증가시키지 않음
                        outboxMessageRepository.updateStatus(it, OutboxMessageStatus.PENDING, false)
                        logger.info("Message ID {} marked as PENDING for retry.", it)
                    }
                } catch (e: Exception) {
                    logger.error(
                        "Error resetting failed message to PENDING: {}, Error: {}",
                        message.id,
                        e.message,
                        e,
                    )
                }
            }
        } catch (e: Exception) {
            logger.error("Error in failed message retry process", e)
        }
    }
}
