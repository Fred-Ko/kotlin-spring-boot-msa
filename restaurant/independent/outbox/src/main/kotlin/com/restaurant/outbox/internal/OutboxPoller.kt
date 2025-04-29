package com.restaurant.outbox.internal

import com.restaurant.outbox.infrastructure.kafka.OutboxMessageSender
import com.restaurant.outbox.port.OutboxMessageRepository
import com.restaurant.outbox.port.model.OutboxMessage
import com.restaurant.outbox.port.model.OutboxMessageStatus
import org.slf4j.LoggerFactory
import org.springframework.scheduling.annotation.Scheduled
import org.springframework.stereotype.Component
import java.util.concurrent.TimeUnit

/**
 * OutboxPoller: 주기적으로 Outbox 테이블에서 PENDING 메시지를 조회하여 Kafka로 전송하고 상태를 갱신한다.
 */
@Component
class OutboxPoller(
    private val outboxMessageRepository: OutboxMessageRepository,
    private val outboxMessageSender: OutboxMessageSender,
) {
    private val log = LoggerFactory.getLogger(javaClass)

    /**
     * 주기적으로 실행되어 PENDING 상태의 메시지를 전송한다.
     * cron/interval 설정은 application-outbox.yml 등에서 조정 가능.
     */
    @Scheduled(fixedDelay = 5000, timeUnit = TimeUnit.MILLISECONDS)
    fun pollAndSend() {
        val pendingMessages: List<OutboxMessage> =
            outboxMessageRepository.findAndMarkForProcessing(
                OutboxMessageStatus.PENDING,
                100,
            )
        if (pendingMessages.isEmpty()) return

        log.info("Found {} pending outbox messages. Attempting to send...", pendingMessages.size)
        for (message in pendingMessages) {
            try {
                outboxMessageSender.send(message)
                outboxMessageRepository.updateStatus(
                    message.id,
                    OutboxMessageStatus.SENT,
                )
                log.info("Successfully sent outbox message: {}", message.id)
            } catch (ex: Exception) {
                outboxMessageRepository.updateStatus(
                    message.id,
                    OutboxMessageStatus.FAILED,
                    incrementRetry = true,
                )
                log.error("Failed to send outbox message: {}. Marked as FAILED.", message.id, ex)
            }
        }
    }
}
