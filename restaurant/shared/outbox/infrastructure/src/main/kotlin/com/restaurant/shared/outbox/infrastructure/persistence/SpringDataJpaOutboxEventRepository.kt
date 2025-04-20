package com.restaurant.shared.outbox.infrastructure.persistence

import com.restaurant.shared.outbox.infrastructure.entity.OutboxEventEntity
import jakarta.persistence.LockModeType
import jakarta.persistence.QueryHint
import org.springframework.data.domain.Pageable
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.Lock
import org.springframework.data.jpa.repository.Query
import org.springframework.data.jpa.repository.QueryHints
import org.springframework.data.repository.query.Param
import org.springframework.stereotype.Repository

/**
 * Spring Data JPA repository for OutboxEventEntity.
 * This is internal to the outbox infrastructure layer.
 */
@Repository
interface SpringDataJpaOutboxEventRepository : JpaRepository<OutboxEventEntity, Long> {
    /**
     * Finds pending or failed outbox events with pessimistic locking to prevent concurrent processing.
     * Uses SKIP LOCKED hint suitable for databases like PostgreSQL and MySQL 8+.
     *
     * @param statuses List of statuses to fetch (e.g., PENDING, FAILED).
     * @param pageable Pageable object to limit the number of fetched events per poll.
     * @return List of locked OutboxEventEntity objects.
     */
    @Lock(LockModeType.PESSIMISTIC_WRITE) // Request a pessimistic write lock
    @QueryHints(
        value = [
            // Specific hint for SKIP LOCKED depends on the database.
            // For PostgreSQL/Oracle:
            QueryHint(name = "jakarta.persistence.lock.timeout", value = "0"), // Equivalent to NOWAIT or SKIP LOCKED behavior
            // For MySQL 8+:
            // QueryHint(name = "jakarta.persistence.lock.scope", value = "EXTENDED"), // May be needed for SKIP LOCKED
            // QueryHint(name = "org.hibernate.lockMode.alias", value = "SKIP_LOCKED") // Check Hibernate documentation for exact syntax
        ],
    )
    @Query("SELECT o FROM OutboxEventEntity o WHERE o.status IN :statuses ORDER BY o.occurredAt ASC")
    fun findByStatusInWithLock(
        @Param("statuses") statuses: List<String>,
        pageable: Pageable,
    ): List<OutboxEventEntity>
}
