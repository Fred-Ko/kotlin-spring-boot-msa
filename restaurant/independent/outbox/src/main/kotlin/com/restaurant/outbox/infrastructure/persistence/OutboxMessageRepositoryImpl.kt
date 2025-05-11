package com.restaurant.outbox.infrastructure.persistence

import com.restaurant.outbox.application.port.OutboxMessageRepository
import com.restaurant.outbox.application.port.model.OutboxMessage
import com.restaurant.outbox.application.port.model.OutboxMessageStatus
import com.restaurant.outbox.infrastructure.persistence.extensions.toNewEntity
import com.restaurant.outbox.infrastructure.persistence.extensions.toDomain
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
        val entity = message.toNewEntity()
        val savedEntity = jpaOutboxMessageRepository.save(entity)
        return savedEntity.toDomain()
    }

    @Transactional
    override fun saveAll(messages: List<OutboxMessage>) {
        val entities = messages.map { it.toNewEntity() }
        jpaOutboxMessageRepository.saveAll(entities)
    }

    @Transactional(readOnly = true)
    override fun findById(id: Long): OutboxMessage? =
        jpaOutboxMessageRepository
            .findById(id)
            .map { it.toDomain() }
            .orElse(null)

    @Transactional(readOnly = true)
    override fun findByStatus(status: OutboxMessageStatus): List<OutboxMessage> =
        jpaOutboxMessageRepository
            .findByStatus(status)
            .map { it.toDomain() }

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
        return savedEntity.toDomain()
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
            ).map { it.toDomain() }

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
                savedEntity.toDomain()
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
        return savedEntity.toDomain()
    }

    @Transactional(readOnly = true)
    override fun findUnprocessedMessages(batchSize: Int): List<OutboxMessage> =
        TODO("JpaOutboxMessageRepository.findUnprocessedMessages(batchSize) 구현 필요")

    @Transactional
    override fun updateMessageStatus(messageId: Long, status: OutboxMessageStatus, retryCount: Int) {
        TODO("JpaOutboxMessageRepository.updateMessageStatus(messageId, status, retryCount) 구현 필요")
    }

    @Transactional(readOnly = true)
    override fun findByStatusAndRetryCountLessThan(
        status: OutboxMessageStatus,
        maxRetries: Int,
        limit: Int
    ): List<OutboxMessage> =
        TODO("JpaOutboxMessageRepository.findByStatusAndRetryCountLessThan(status, maxRetries, limit) 구현 필요")
}

