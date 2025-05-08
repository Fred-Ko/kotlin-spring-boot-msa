package com.restaurant.outbox.application

import com.restaurant.outbox.application.port.OutboxMessageRepository
import com.restaurant.outbox.application.port.model.OutboxMessageStatus
import com.restaurant.outbox.infrastructure.kafka.OutboxMessageSender
import mu.KotlinLogging
import org.springframework.scheduling.annotation.Scheduled
import org.springframework.stereotype.Component
import org.springframework.transaction.annotation.Transactional

private val logger = KotlinLogging.logger {}

/**
 * OutboxPoller: 주기적으로 Outbox 테이블에서 PENDING 메시지를 조회하여 Kafka로 전송하고 상태를 갱신한다.
 */
@Component
class OutboxPoller(
    private val outboxMessageRepository: OutboxMessageRepository,
    private val outboxMessageSender: OutboxMessageSender,
) {
    /**
     * 주기적으로 실행되어 PENDING 상태의 메시지를 전송한다.
     * cron/interval 설정은 application-outbox.yml 등에서 조정 가능.
     */
    @Scheduled(fixedDelay = 1000)
    @Transactional
    fun pollMessages() {
        try {
            val messages =
                outboxMessageRepository.findAndMarkForProcessing(
                    status = OutboxMessageStatus.PENDING,
                    limit = 10,
                )

            if (messages.isEmpty()) {
                return
            }

            logger.debug { "Found ${messages.size} messages to process" }

            messages.forEach { message ->
                try {
                    outboxMessageSender.send(message)
                    outboxMessageRepository.updateStatus(
                        id = message.dbId!!,
                        newStatus = OutboxMessageStatus.SENT,
                    )
                } catch (e: Exception) {
                    logger.error(e) { "Failed to process message: $message" }
                    outboxMessageRepository.updateStatus(
                        id = message.dbId!!,
                        newStatus = OutboxMessageStatus.FAILED,
                        incrementRetry = true,
                    )
                }
            }
        } catch (e: Exception) {
            logger.error(e) { "Failed to poll messages" }
        }
    }
}
