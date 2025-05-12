package com.restaurant.outbox.infrastructure.persistence.repository

import com.restaurant.outbox.application.port.OutboxMessageRepository
import com.restaurant.outbox.application.port.model.OutboxMessage
import com.restaurant.outbox.infrastructure.entity.OutboxEventEntity
import com.restaurant.outbox.application.port.model.OutboxMessageStatus
import com.restaurant.outbox.infrastructure.persistence.extensions.toDomain
import com.restaurant.outbox.infrastructure.persistence.extensions.toNewEntity
import org.springframework.data.domain.PageRequest
import org.springframework.data.domain.Pageable
import org.springframework.stereotype.Repository
import org.springframework.transaction.annotation.Transactional
import java.time.Instant

@Repository
class OutboxMessageRepositoryImpl(
    private val jpaOutboxEventRepository: JpaOutboxEventRepository,
) : OutboxMessageRepository {

    @Transactional
    override fun save(message: OutboxMessage): OutboxMessage {
        val entity = message.toNewEntity() 
        val savedEntity = jpaOutboxEventRepository.save(entity)
        return savedEntity.toDomain() 
    }

    @Transactional
    override fun saveAll(messages: List<OutboxMessage>) { 
        val entities = messages.map { it.toNewEntity() } 
        jpaOutboxEventRepository.saveAll(entities)
    }

    @Transactional(readOnly = true)
    override fun findById(id: Long): OutboxMessage? {
        return jpaOutboxEventRepository.findById(id).map { it.toDomain() }.orElse(null) 
    }

    @Transactional(readOnly = true)
    override fun findByStatus(status: OutboxMessageStatus): List<OutboxMessage> {
        return jpaOutboxEventRepository.findByStatus(status).map { it.toDomain() } 
    }

    @Transactional
    override fun updateStatus(
        id: Long,
        newStatus: OutboxMessageStatus,
        incrementRetry: Boolean, 
    ): OutboxMessage? {
        val entity = jpaOutboxEventRepository.findById(id).orElse(null) ?: return null
        entity.status = newStatus
        if (incrementRetry) {
            entity.retryCount += 1
        }
        entity.lastAttemptTime = Instant.now()
        entity.updatedAt = Instant.now()
        val savedEntity = jpaOutboxEventRepository.save(entity)
        return savedEntity.toDomain() 
    }

    @Transactional(readOnly = true)
    override fun findFailedMessagesExceedingRetryCount(
        maxRetries: Int,
        limit: Int,
    ): List<OutboxMessage> {
        return jpaOutboxEventRepository.findByStatusAndRetryCountGreaterThanEqual(
            OutboxMessageStatus.FAILED,
            maxRetries,
            PageRequest.of(0, limit)
        ).map { it.toDomain() } 
    }

    @Transactional
    override fun findAndMarkForProcessing(
        status: OutboxMessageStatus,
        limit: Int,
    ): List<OutboxMessage> {
        val pageable: Pageable = PageRequest.of(0, limit)
        val entitiesToProcess = if (status == OutboxMessageStatus.PENDING) {
            jpaOutboxEventRepository.findUnprocessedEventsWithLock(pageable)
        } else {
            jpaOutboxEventRepository.findTopByStatusOrderByCreatedAtAsc(status, pageable)
        }

        return entitiesToProcess.mapNotNull { entity: OutboxEventEntity ->
            try {
                entity.status = OutboxMessageStatus.PROCESSING
                entity.lastAttemptTime = Instant.now()
                entity.updatedAt = Instant.now()
                val savedEntity = jpaOutboxEventRepository.save(entity)
                savedEntity.toDomain() 
            } catch (e: Exception) {
                
                null
            }
        }
    }

    @Transactional(readOnly = true)
    override fun countByStatus(status: OutboxMessageStatus): Long {
        return jpaOutboxEventRepository.countByStatus(status)
    }

    @Transactional
    override fun incrementRetryCount(id: Long): OutboxMessage? {
        val entity = jpaOutboxEventRepository.findById(id).orElse(null) ?: return null
        entity.retryCount += 1
        entity.lastAttemptTime = Instant.now()
        entity.updatedAt = Instant.now()
        val savedEntity = jpaOutboxEventRepository.save(entity)
        return savedEntity.toDomain() 
    }

    @Transactional(readOnly = true)
    override fun findUnprocessedMessages(batchSize: Int): List<OutboxMessage> {
        return jpaOutboxEventRepository
            .findTopByStatusOrderByCreatedAtAsc(OutboxMessageStatus.PENDING, PageRequest.of(0, batchSize))
            .map { it.toDomain() } 
    }

    @Transactional
    override fun updateMessageStatus(
        messageId: Long,
        status: OutboxMessageStatus,
        retryCount: Int,
    ) {
        val entity =
            jpaOutboxEventRepository.findById(messageId).orElseThrow {
                IllegalArgumentException("Message not found with id: $messageId")
            }

        entity.status = status
        entity.retryCount = retryCount
        entity.lastAttemptTime = Instant.now()
        entity.updatedAt = Instant.now()

        jpaOutboxEventRepository.save(entity)
    }

    @Transactional(readOnly = true)
    override fun findByStatusAndRetryCountLessThan(
        status: OutboxMessageStatus,
        maxRetries: Int,
        limit: Int
    ): List<OutboxMessage> {
        val pageable: Pageable = PageRequest.of(0, limit)
        return jpaOutboxEventRepository.findByStatusAndRetryCountLessThan(status, maxRetries, pageable)
            .map { it.toDomain() } 
    }
}
