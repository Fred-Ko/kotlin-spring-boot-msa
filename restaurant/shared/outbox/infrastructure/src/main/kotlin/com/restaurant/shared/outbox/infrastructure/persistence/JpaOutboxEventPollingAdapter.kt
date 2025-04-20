package com.restaurant.shared.outbox.infrastructure.persistence

import com.restaurant.shared.outbox.application.dto.OutboxEventPollingDto
import com.restaurant.shared.outbox.application.port.OutboxEventPollingPort
import com.restaurant.shared.outbox.infrastructure.persistence.extensions.toPollingDto
import org.springframework.data.domain.Pageable
import org.springframework.stereotype.Component
import java.time.LocalDateTime

@Component
class JpaOutboxEventPollingAdapter(
    private val jpaRepository: SpringDataJpaOutboxEventRepository,
) : OutboxEventPollingPort {
    override fun findEventsForProcessing(
        statuses: List<String>,
        pageable: Pageable,
    ): List<OutboxEventPollingDto> = jpaRepository.findByStatusInWithLock(statuses, pageable).map { it.toPollingDto() }

    override fun updateEventStatus(
        eventId: Long,
        status: String,
        processedAt: LocalDateTime?,
        errorMessage: String?,
        retryCount: Int?,
        lastAttemptTime: LocalDateTime?,
    ): OutboxEventPollingDto? {
        val entity = jpaRepository.findById(eventId).orElse(null) ?: return null
        entity.status = status
        processedAt?.let { entity.processedAt = it }
        errorMessage?.let { entity.errorMessage = it }
        retryCount?.let { entity.retryCount = it }
        lastAttemptTime?.let { entity.lastAttemptTime = it }
        return jpaRepository.save(entity).toPollingDto()
    }
}
