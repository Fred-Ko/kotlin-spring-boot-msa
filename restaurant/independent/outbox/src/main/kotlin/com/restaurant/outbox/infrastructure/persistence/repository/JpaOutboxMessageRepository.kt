package com.restaurant.outbox.infrastructure.persistence.repository

import com.restaurant.outbox.application.port.model.OutboxMessageStatus
import com.restaurant.outbox.infrastructure.entity.OutboxEventEntity
import jakarta.persistence.LockModeType
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.Lock
import org.springframework.data.jpa.repository.Query
import org.springframework.data.repository.query.Param
import java.time.Instant

interface JpaOutboxMessageRepository : JpaRepository<OutboxEventEntity, Long> {
    @Lock(LockModeType.PESSIMISTIC_WRITE)
    @Query(
        """
        SELECT e FROM OutboxEventEntity e 
        WHERE e.status = :status 
        AND (e.lastAttemptTime IS NULL OR e.lastAttemptTime < :cutoffTime)
        AND e.retryCount < :maxRetries
        ORDER BY e.createdAt ASC
        """,
        nativeQuery = false,
    )
    fun findMessagesToProcess(
        @Param("status") status: OutboxMessageStatus,
        @Param("cutoffTime") cutoffTime: Instant,
        @Param("maxRetries") maxRetries: Int = 3,
    ): List<OutboxEventEntity>

    fun findByStatus(status: OutboxMessageStatus): List<OutboxEventEntity>

    @Query("SELECT COUNT(e) FROM OutboxEventEntity e WHERE e.status = :status")
    fun countByStatus(
        @Param("status") status: OutboxMessageStatus,
    ): Long

    @Lock(LockModeType.PESSIMISTIC_WRITE)
    @Query(
        """
        SELECT e FROM OutboxEventEntity e 
        WHERE e.status = :status 
        AND e.retryCount >= :maxRetries
        ORDER BY e.createdAt ASC
        """,
        nativeQuery = false,
    )
    fun findFailedMessagesExceedingRetryCount(
        @Param("status") status: OutboxMessageStatus,
        @Param("maxRetries") maxRetries: Int,
        @Param("limit") limit: Int,
    ): List<OutboxEventEntity>
}
