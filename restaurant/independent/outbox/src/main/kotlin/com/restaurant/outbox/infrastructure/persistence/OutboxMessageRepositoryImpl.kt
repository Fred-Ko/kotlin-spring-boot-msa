package com.restaurant.outbox.infrastructure.persistence

import com.restaurant.outbox.application.port.OutboxMessageRepository
import com.restaurant.outbox.application.port.model.OutboxMessage
import com.restaurant.outbox.application.port.model.OutboxMessageStatus
import com.restaurant.outbox.infrastructure.persistence.extensions.toOutboxEventEntity
import com.restaurant.outbox.infrastructure.persistence.extensions.toOutboxMessage
import com.restaurant.outbox.infrastructure.persistence.repository.JpaOutboxMessageRepository
import org.springframework.stereotype.Repository
import org.springframework.transaction.annotation.Transactional
import java.time.Instant

@Repository
class OutboxMessageRepositoryImpl(
    private val jpaOutboxMessageRepository: JpaOutboxMessageRepository,
) : OutboxMessageRepository {
    @Transactional
    override fun save(message: OutboxMessage): OutboxMessage {
        val entity = message.toOutboxEventEntity()
        val savedEntity = jpaOutboxMessageRepository.save(entity)
        return savedEntity.toOutboxMessage()
    }

    @Transactional
    override fun saveAll(messages: List<OutboxMessage>): List<OutboxMessage> {
        val entities = messages.map { it.toOutboxEventEntity() }
        val savedEntities = jpaOutboxMessageRepository.saveAll(entities)
        return savedEntities.map { it.toOutboxMessage() }
    }

    @Transactional
    override fun save(messages: List<OutboxMessage>) {
        val entities = messages.map { it.toOutboxEventEntity() }
        jpaOutboxMessageRepository.saveAll(entities)
    }

    @Transactional(readOnly = true)
    override fun findById(id: Long): OutboxMessage? =
        jpaOutboxMessageRepository
            .findById(id)
            .map { it.toOutboxMessage() }
            .orElse(null)

    @Transactional(readOnly = true)
    override fun findByStatus(status: OutboxMessageStatus): List<OutboxMessage> =
        jpaOutboxMessageRepository
            .findByStatus(status)
            .map { it.toOutboxMessage() }

    @Transactional
    override fun updateStatus(
        id: Long,
        newStatus: OutboxMessageStatus,
        incrementRetry: Boolean,
    ): OutboxMessage? {
        val entity = jpaOutboxMessageRepository.findById(id).orElse(null) ?: return null
        entity.status = newStatus
        if (incrementRetry) {
            entity.retryCount++
        }
        entity.lastAttemptTime = Instant.now()
        val savedEntity = jpaOutboxMessageRepository.save(entity)
        return savedEntity.toOutboxMessage()
    }

    @Transactional(readOnly = true)
    override fun findFailedMessagesExceedingRetryCount(
        maxRetries: Int,
        limit: Int,
    ): List<OutboxMessage> =
        jpaOutboxMessageRepository
            .findFailedMessagesExceedingRetryCount(
                status = OutboxMessageStatus.FAILED,
                maxRetries = maxRetries,
                limit = limit,
            ).map { it.toOutboxMessage() }

    @Transactional
    override fun findAndMarkForProcessing(
        status: OutboxMessageStatus,
        limit: Int,
    ): List<OutboxMessage> {
        val cutoffTime = Instant.now().minusSeconds(30)
        return jpaOutboxMessageRepository
            .findMessagesToProcess(status, cutoffTime)
            .map { entity ->
                entity.status = OutboxMessageStatus.PROCESSING
                entity.lastAttemptTime = Instant.now()
                val savedEntity = jpaOutboxMessageRepository.save(entity)
                savedEntity.toOutboxMessage()
            }
    }

    @Transactional(readOnly = true)
    override fun countByStatus(status: OutboxMessageStatus): Long = jpaOutboxMessageRepository.countByStatus(status)

    @Transactional
    override fun incrementRetryCount(id: Long): OutboxMessage? {
        val entity = jpaOutboxMessageRepository.findById(id).orElse(null) ?: return null
        entity.retryCount++
        entity.lastAttemptTime = Instant.now()
        val savedEntity = jpaOutboxMessageRepository.save(entity)
        return savedEntity.toOutboxMessage()
    }
}
