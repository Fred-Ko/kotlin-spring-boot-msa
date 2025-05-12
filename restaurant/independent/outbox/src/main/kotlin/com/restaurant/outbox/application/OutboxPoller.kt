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

    @Scheduled(fixedRate = 1000) 
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
                        
                        outboxMessageRepository.updateStatus(it, OutboxMessageStatus.PENDING, true)
                    }
                } catch (e: Exception) { 
                    logger.error(
                        "Unexpected error processing message: {}, Error: {}",
                        message.id,
                        e.message,
                        e,
                    )
                    message.id?.let {
                        
                        outboxMessageRepository.updateStatus(it, OutboxMessageStatus.FAILED, true)
                    }
                }
            }
        } catch (e: Exception) {
            logger.error("Error in outbox polling process", e)
        }
    }

    @Scheduled(fixedRate = 300000) 
    @Transactional
    fun retryFailedMessages() {
        try {
            
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
