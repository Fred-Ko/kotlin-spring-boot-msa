package com.restaurant.outbox.infrastructure.persistence

import com.restaurant.outbox.infrastructure.exception.OutboxException
import com.restaurant.outbox.infrastructure.persistence.entity.OutboxEventEntity
import com.restaurant.outbox.infrastructure.persistence.extensions.toDomainModel
import com.restaurant.outbox.infrastructure.persistence.extensions.toEntity
import com.restaurant.outbox.port.OutboxMessageRepository
import com.restaurant.outbox.port.model.OutboxMessage
import com.restaurant.outbox.port.model.OutboxMessageStatus
import jakarta.persistence.LockModeType
import mu.KotlinLogging
import org.springframework.data.domain.Pageable
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.Lock
import org.springframework.data.jpa.repository.Modifying
import org.springframework.data.jpa.repository.Query
import org.springframework.data.repository.findByIdOrNull
import org.springframework.stereotype.Repository
import org.springframework.transaction.annotation.Transactional
import java.time.Instant
import java.util.UUID

private val log = KotlinLogging.logger {}

/**
 * Interface combining JpaRepository and custom methods for OutboxEventEntity.
 * This interface will be implemented by Spring Data JPA. Uses UUID as ID type.
 */
interface JpaOutboxMessageRepository :
    JpaRepository<OutboxEventEntity, UUID>,
    OutboxMessageRepositoryCustom {
    fun findByStatusOrderByCreatedAtAsc(
        status: OutboxMessageStatus,
        pageable: Pageable,
    ): List<OutboxEventEntity> // Use Pageable for limit

    fun countByStatus(status: OutboxMessageStatus): Long

    fun findByStatusAndRetryCountGreaterThanOrderByCreatedAtAsc(
        status: OutboxMessageStatus,
        retryCount: Int,
        pageable: Pageable,
    ): List<OutboxEventEntity> // Use Pageable for limit

    // Custom query for finding and locking with SKIP LOCKED (PostgreSQL specific example)
    // Adjust the query for your specific database if not PostgreSQL
    @Lock(LockModeType.PESSIMISTIC_WRITE) // Basic pessimistic lock, DB specific needed for SKIP LOCKED
    @Query(
        value = """
            SELECT o.* FROM outbox_events o
            WHERE o.status = :#{#status.name()}
            ORDER BY o.created_at ASC
            LIMIT :limit FOR UPDATE SKIP LOCKED
        """,
        nativeQuery = true,
    )
    fun findAndLockByStatusNativeSkipLocked(
        status: OutboxMessageStatus,
        limit: Int,
    ): List<OutboxEventEntity>

    @Modifying
    @Transactional // Add Transactional for modifying queries
    @Query("UPDATE OutboxEventEntity o SET o.status = :newStatus, o.updatedAt = :now WHERE o.id = :id")
    fun updateStatus(
        id: UUID,
        newStatus: OutboxMessageStatus,
        now: Instant,
    ): Int

    @Modifying
    @Transactional // Add Transactional for modifying queries
    @Query(
        "UPDATE OutboxEventEntity o SET o.status = :newStatus, " +
            "o.retryCount = o.retryCount + 1, " +
            "o.lastAttemptTime = :now, o.updatedAt = :now WHERE o.id = :id",
    )
    fun updateStatusAndIncrementRetry(
        id: UUID,
        newStatus: OutboxMessageStatus,
        now: Instant,
    ): Int
}

/**
 * Custom methods separated for clarity, implemented below.
 */
interface OutboxMessageRepositoryCustom {
    fun findAndLockByStatus(
        status: OutboxMessageStatus,
        limit: Int,
    ): List<OutboxEventEntity>
    // Add other custom methods if needed
}

/**
 * Implementation of the Port interface and custom methods.
 * Uses the Spring Data JPA repository for database operations.
 */
@Repository("jpaOutboxMessageRepositoryImpl") // Changed bean name slightly for clarity
class JpaOutboxMessageRepositoryImpl(
    private val springDataRepo: JpaOutboxMessageRepository,
) : OutboxMessageRepository,
    OutboxMessageRepositoryCustom { // Implement both Port and Custom
    @Transactional
    override fun save(message: OutboxMessage): OutboxMessage {
        log.debug { "Saving single outbox message: aggregateId=${message.aggregateId}, topic=${message.topic}" }
        try {
            val entity = message.toEntity()
            val savedEntity = springDataRepo.save(entity)
            return savedEntity.toDomainModel()
        } catch (e: Exception) {
            log.error(e) { "Error saving single outbox message with aggregateId ${message.aggregateId}" }
            throw OutboxException.DatabaseOperationException(message = "Failed to save outbox message", cause = e)
        }
    }

    @Transactional
    override fun saveAll(messages: List<OutboxMessage>): List<OutboxMessage> {
        if (messages.isEmpty()) return emptyList()
        log.debug { "Saving ${messages.size} outbox message(s). First aggregateId=${messages.firstOrNull()?.aggregateId}" }
        try {
            val entities = messages.map { it.toEntity() }
            val savedEntities = springDataRepo.saveAll(entities)
            return savedEntities.map { it.toDomainModel() }
        } catch (e: Exception) {
            log.error(e) { "Error saving batch of ${messages.size} outbox messages" }
            throw OutboxException.DatabaseOperationException(message = "Failed to save batch of outbox messages", cause = e)
        }
    }

    @Transactional(readOnly = true)
    override fun findById(id: UUID): OutboxMessage? {
        log.trace { "Finding outbox message by ID: $id" }
        return springDataRepo.findByIdOrNull(id)?.toDomainModel()
    }

    @Transactional(readOnly = true)
    override fun findByStatus(
        status: OutboxMessageStatus,
        limit: Int,
    ): List<OutboxMessage> =
        springDataRepo
            .findByStatusOrderByCreatedAtAsc(status, Pageable.ofSize(limit))
            .map { it.toDomainModel() }

    // Implementation of findAndLockByStatus using the native query
    @Transactional
    override fun findAndLockByStatus(
        status: OutboxMessageStatus,
        limit: Int,
    ): List<OutboxEventEntity> {
        log.trace { "Finding and locking up to $limit messages with status $status." }
        try {
            // Use the native query with SKIP LOCKED
            val lockedEntities: List<OutboxEventEntity> = springDataRepo.findAndLockByStatusNativeSkipLocked(status, limit)
            log.debug { "Found and locked ${lockedEntities.size} messages with status $status." }
            return lockedEntities
        } catch (e: Exception) {
            log.error(e) { "Database error finding and locking messages with status $status" }
            throw OutboxException.DatabaseOperationException(message = "Failed to find/lock messages with status $status", cause = e)
        }
    }

    @Transactional
    override fun updateStatus(
        id: UUID,
        newStatus: OutboxMessageStatus,
        incrementRetry: Boolean,
    ): OutboxMessage? {
        log.debug { "Updating status for outbox message $id to $newStatus (incrementRetry=$incrementRetry)" }
        try {
            val entity = springDataRepo.findByIdOrNull(id) ?: return null
            entity.status = newStatus
            entity.updatedAt = Instant.now()
            if (incrementRetry) {
                entity.retryCount += 1
                entity.lastAttemptTime = Instant.now()
            }
            val saved = springDataRepo.save(entity)
            return saved.toDomainModel()
        } catch (e: Exception) {
            log.error(e) { "Error updating status for outbox message $id to $newStatus" }
            throw OutboxException.DatabaseOperationException(message = "Failed to update outbox message status", cause = e)
        }
    }

    @Transactional
    override fun findAndMarkForProcessing(
        status: OutboxMessageStatus,
        limit: Int,
    ): List<OutboxMessage> {
        log.debug { "Finding and marking up to $limit messages with status $status for processing." }
        try {
            val lockedEntities = springDataRepo.findAndLockByStatusNativeSkipLocked(status, limit)
            lockedEntities.forEach {
                it.status = OutboxMessageStatus.PROCESSING
                it.updatedAt = Instant.now()
            }
            val saved = springDataRepo.saveAll(lockedEntities)
            return saved.map { it.toDomainModel() }
        } catch (e: Exception) {
            log.error(e) { "Database error finding/marking messages with status $status" }
            throw OutboxException.DatabaseOperationException(message = "Failed to find/mark messages with status $status", cause = e)
        }
    }

    @Transactional
    override fun incrementRetryCount(id: UUID): OutboxMessage? {
        log.debug { "Incrementing retry count for outbox message $id" }
        try {
            val entity = springDataRepo.findByIdOrNull(id) ?: return null
            entity.retryCount += 1
            entity.lastAttemptTime = Instant.now()
            entity.updatedAt = Instant.now()
            val saved = springDataRepo.save(entity)
            return saved.toDomainModel()
        } catch (e: Exception) {
            log.error(e) { "Error incrementing retry count for outbox message $id" }
            throw OutboxException.DatabaseOperationException(message = "Failed to increment retry count", cause = e)
        }
    }

    @Transactional(readOnly = true)
    override fun countByStatus(status: OutboxMessageStatus): Long = springDataRepo.countByStatus(status)

    @Transactional(readOnly = true)
    override fun findFailedMessagesExceedingRetryCount(
        maxRetries: Int,
        limit: Int,
    ): List<OutboxMessage> =
        springDataRepo
            .findByStatusAndRetryCountGreaterThanOrderByCreatedAtAsc(
                OutboxMessageStatus.FAILED,
                maxRetries,
                Pageable.ofSize(limit),
            ).map { it.toDomainModel() }
}
