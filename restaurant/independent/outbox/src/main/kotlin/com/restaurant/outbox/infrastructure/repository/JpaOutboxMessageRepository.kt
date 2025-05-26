package com.restaurant.outbox.infrastructure.repository

import com.restaurant.outbox.application.dto.model.OutboxMessageStatus
import com.restaurant.outbox.infrastructure.entity.OutboxEventEntity
import jakarta.persistence.LockModeType
import org.springframework.data.domain.Pageable
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

    fun findTopByStatusOrderByCreatedAtAsc(
        status: OutboxMessageStatus,
        pageable: Pageable,
    ): List<OutboxEventEntity>

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
    fun findByStatusAndRetryCountGreaterThanEqual(
        @Param("status") status: OutboxMessageStatus,
        @Param("maxRetries") maxRetries: Int,
        pageable: Pageable,
    ): List<OutboxEventEntity>

    @Query(
        "SELECT oe FROM OutboxEventEntity oe WHERE oe.status = :status AND oe.retryCount < :maxRetries ORDER BY oe.createdAt ASC",
    )
    fun findByStatusAndRetryCountLessThan(
        @Param("status") status: OutboxMessageStatus,
        @Param("maxRetries") maxRetries: Int,
        pageable: Pageable,
    ): List<OutboxEventEntity>

    @Lock(LockModeType.PESSIMISTIC_WRITE)
    @Query(
        "SELECT e FROM OutboxEventEntity e WHERE e.status = com.restaurant.outbox.application.dto.model.OutboxMessageStatus.PENDING ORDER BY e.createdAt ASC",
    )
    fun findUnprocessedEventsWithLock(pageable: Pageable): List<OutboxEventEntity>
}
