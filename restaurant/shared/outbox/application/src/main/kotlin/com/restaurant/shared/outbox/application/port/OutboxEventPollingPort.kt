package com.restaurant.shared.outbox.application.port

import com.restaurant.shared.outbox.application.dto.OutboxEventPollingDto
import org.springframework.data.domain.Pageable
import java.time.LocalDateTime

interface OutboxEventPollingPort {
    fun findEventsForProcessing(
        statuses: List<String>,
        pageable: Pageable,
    ): List<OutboxEventPollingDto>

    fun updateEventStatus(
        eventId: Long,
        status: String,
        processedAt: LocalDateTime? = null,
        errorMessage: String? = null,
        retryCount: Int? = null,
        lastAttemptTime: LocalDateTime? = null,
    ): OutboxEventPollingDto?
}
