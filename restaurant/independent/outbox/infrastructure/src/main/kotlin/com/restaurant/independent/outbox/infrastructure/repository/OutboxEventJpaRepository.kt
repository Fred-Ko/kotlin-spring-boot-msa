package com.restaurant.independent.outbox.infrastructure.repository

import com.restaurant.independent.outbox.infrastructure.entity.OutboxEventEntity
import jakarta.persistence.LockModeType
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.Lock
import org.springframework.data.jpa.repository.Query
import org.springframework.stereotype.Repository
import java.time.Instant

@Repository
interface OutboxEventJpaRepository : JpaRepository<OutboxEventEntity, Long> {
    @Lock(LockModeType.PESSIMISTIC_WRITE)
    @Query(
        """
        SELECT e FROM OutboxEventEntity e 
        WHERE e.status = 'PENDING' 
        AND (e.lastAttemptTime IS NULL OR e.lastAttemptTime < :cutoffTime)
        AND e.retryCount < :maxRetries
        ORDER BY e.createdAt ASC
        """,
    )
    fun findPendingEventsForProcessing(
        cutoffTime: Instant,
        maxRetries: Int,
    ): List<OutboxEventEntity>
}
