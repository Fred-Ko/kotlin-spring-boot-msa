package com.restaurant.independent.outbox.infrastructure.persistence

import com.restaurant.independent.outbox.application.error.OutboxStorageException
import com.restaurant.independent.outbox.application.port.OutboxMessageRepository
import com.restaurant.independent.outbox.application.port.model.OutboxMessage
import com.restaurant.independent.outbox.application.port.model.OutboxMessageStatus
import com.restaurant.independent.outbox.infrastructure.repository.JpaOutboxMessageRepository
import org.springframework.stereotype.Repository
import org.springframework.transaction.annotation.Transactional
import java.time.Instant
import java.util.UUID

/**
 * OutboxMessageRepository 인터페이스의 JPA 구현체.
 */
@Repository
class OutboxMessageRepositoryImpl(
    private val jpaOutboxMessageRepository: JpaOutboxMessageRepository,
) : OutboxMessageRepository {
    @Transactional
    override fun save(message: OutboxMessage): OutboxMessage {
        try {
            val entity = jpaOutboxMessageRepository.save(message.toEntity())
            return entity.toDomainModel()
        } catch (e: Exception) {
            throw OutboxStorageException(
                message = "Failed to save outbox message: ${e.message}",
                cause = e,
            )
        }
    }

    @Transactional
    override fun saveAll(messages: List<OutboxMessage>): List<OutboxMessage> {
        try {
            val entities = jpaOutboxMessageRepository.saveAll(messages.map { it.toEntity() })
            return entities.map { it.toDomainModel() }
        } catch (e: Exception) {
            throw OutboxStorageException(
                message = "Failed to save outbox messages: ${e.message}",
                cause = e,
            )
        }
    }

    @Transactional(readOnly = true)
    override fun findById(id: UUID): OutboxMessage? =
        jpaOutboxMessageRepository
            .findById(id)
            .map { it.toDomainModel() }
            .orElse(null)

    @Transactional(readOnly = true)
    override fun findByStatus(
        status: OutboxMessageStatus,
        limit: Int,
    ): List<OutboxMessage> =
        jpaOutboxMessageRepository
            .findByStatusOrderByCreatedAtAsc(status, limit)
            .map { it.toDomainModel() }

    @Transactional
    override fun updateStatus(
        id: UUID,
        newStatus: OutboxMessageStatus,
        incrementRetry: Boolean,
    ): OutboxMessage? {
        val entity =
            jpaOutboxMessageRepository.findById(id).orElse(null)
                ?: return null

        val updatedEntity =
            entity.copy(
                status = newStatus,
                retryCount = if (incrementRetry) entity.retryCount + 1 else entity.retryCount,
                updatedAt = Instant.now(),
                lastAttemptTime = if (newStatus == OutboxMessageStatus.PROCESSING) Instant.now() else entity.lastAttemptTime,
            )

        return jpaOutboxMessageRepository.save(updatedEntity).toDomainModel()
    }

    @Transactional
    override fun findAndMarkForProcessing(
        status: OutboxMessageStatus,
        limit: Int,
    ): List<OutboxMessage> {
        val entities = jpaOutboxMessageRepository.findAndLockByStatus(status, limit)
        entities.forEach { entity ->
            entity.status = OutboxMessageStatus.PROCESSING
            entity.updatedAt = Instant.now()
            entity.lastAttemptTime = Instant.now()
        }
        return jpaOutboxMessageRepository.saveAll(entities).map { it.toDomainModel() }
    }

    @Transactional(readOnly = true)
    override fun countByStatus(status: OutboxMessageStatus): Long = jpaOutboxMessageRepository.countByStatus(status)

    @Transactional
    override fun incrementRetryCount(id: UUID): OutboxMessage? {
        val entity = jpaOutboxMessageRepository.findByIdOrNull(id) ?: return null
        entity.retryCount++
        entity.updatedAt = Instant.now()
        entity.lastAttemptTime = Instant.now()
        return jpaOutboxMessageRepository.save(entity).toDomainModel()
    }

    @Transactional(readOnly = true)
    override fun findFailedMessagesExceedingRetryCount(
        maxRetries: Int,
        limit: Int,
    ): List<OutboxMessage> =
        jpaOutboxMessageRepository
            .findByStatusAndRetryCountGreaterThanOrderByCreatedAtAsc(
                OutboxMessageStatus.FAILED,
                maxRetries,
                limit,
            ).map { it.toDomainModel() }
}
