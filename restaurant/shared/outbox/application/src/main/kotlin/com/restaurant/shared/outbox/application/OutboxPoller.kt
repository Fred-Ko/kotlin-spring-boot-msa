package com.restaurant.shared.outbox.application

import com.restaurant.shared.outbox.application.dto.OutboxEventPollingDto
import com.restaurant.shared.outbox.application.port.OutboxEventPollingPort
import org.slf4j.LoggerFactory
import org.springframework.data.domain.PageRequest
import org.springframework.scheduling.annotation.Scheduled
import org.springframework.stereotype.Component

@Component
class OutboxPoller(
    private val outboxPollingPort: OutboxEventPollingPort,
    private val eventSender: OutboxEventSender,
) {
    private val log = LoggerFactory.getLogger(javaClass)

    @Scheduled(fixedDelay = 60000)
    fun pollAndSendEvents() {
        try {
            val statuses = listOf("PENDING", "FAILED")
            val events: List<OutboxEventPollingDto> = outboxPollingPort.findEventsForProcessing(statuses, PageRequest.of(0, 100))
            if (events.isNotEmpty()) {
                log.info("폴링된 Outbox 이벤트 ${events.size}건 처리 시작")
                eventSender.processEvents(events)
            }
        } catch (e: Exception) {
            log.error("Outbox 이벤트 폴링 중 오류 발생: ${e.message}", e)
        }
    }
}
