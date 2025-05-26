package com.restaurant.outbox.infrastructure.repository

import com.restaurant.outbox.application.dto.OutboxMessageRepository
import com.restaurant.outbox.application.dto.model.OutboxMessage
import com.restaurant.outbox.application.dto.model.OutboxMessageStatus
import com.restaurant.outbox.infrastructure.entity.OutboxEventEntity
import com.restaurant.outbox.infrastructure.exception.OutboxException
import com.restaurant.outbox.infrastructure.persistence.extensions.toDomain
import com.restaurant.outbox.infrastructure.persistence.extensions.toEntity
import org.slf4j.LoggerFactory
import org.springframework.data.domain.PageRequest
import org.springframework.data.domain.Pageable
import org.springframework.stereotype.Repository
import org.springframework.transaction.annotation.Transactional
import java.time.Instant

/**
 * Outbox 메시지 저장 Repository 구현체
 * Rule 82: Outbox 모듈의 Infrastructure 레이어 내 repository 패키지에 위치
 * Rule 87: 동시성 제어를 위한 데이터베이스 수준 잠금 사용
 */
@Repository
open class OutboxMessageRepositoryImpl(
    private val jpaOutboxEventRepository: JpaOutboxEventRepository,
) : OutboxMessageRepository {
    private val logger = LoggerFactory.getLogger(this::class.java)

    @Transactional
    override fun save(message: OutboxMessage): OutboxMessage {
        try {
            val entity = message.toEntity()
            val savedEntity = jpaOutboxEventRepository.save(entity)
            return savedEntity.toDomain()
        } catch (e: Exception) {
            logger.error("Failed to save outbox message", e)
            throw OutboxException.DatabaseOperationException(
                "Failed to save outbox message",
                e,
            )
        }
    }

    @Transactional
    override fun saveAll(messages: List<OutboxMessage>) {
        try {
            val entities = messages.map { it.toEntity() }
            jpaOutboxEventRepository.saveAll(entities)
            logger.debug("Successfully saved {} outbox messages", messages.size)
        } catch (e: Exception) {
            logger.error("Failed to save {} outbox messages", messages.size, e)
            throw OutboxException.DatabaseOperationException(
                "Failed to save outbox messages",
                e,
            )
        }
    }

    @Transactional(readOnly = true)
    override fun findById(id: Long): OutboxMessage? =
        try {
            jpaOutboxEventRepository.findById(id).map { it.toDomain() }.orElse(null)
        } catch (e: Exception) {
            logger.error("Failed to find outbox message by id: {}", id, e)
            throw OutboxException.DatabaseOperationException(
                "Failed to find outbox message by id: $id",
                e,
            )
        }

    @Transactional(readOnly = true)
    override fun findByStatus(status: OutboxMessageStatus): List<OutboxMessage> =
        try {
            jpaOutboxEventRepository.findByStatus(status).map { it.toDomain() }
        } catch (e: Exception) {
            logger.error("Failed to find outbox messages by status: {}", status, e)
            throw OutboxException.DatabaseOperationException(
                "Failed to find outbox messages by status: $status",
                e,
            )
        }

    @Transactional
    override fun updateStatus(
        id: Long,
        newStatus: OutboxMessageStatus,
        incrementRetry: Boolean,
    ): OutboxMessage? {
        return try {
            val entity = jpaOutboxEventRepository.findById(id).orElse(null) ?: return null
            entity.status = newStatus
            if (incrementRetry) {
                entity.retryCount += 1
            }
            entity.lastAttemptTime = Instant.now()
            entity.updatedAt = Instant.now()
            val savedEntity = jpaOutboxEventRepository.save(entity)
            savedEntity.toDomain()
        } catch (e: Exception) {
            logger.error("Failed to update status for message id: {}", id, e)
            throw OutboxException.DatabaseOperationException(
                "Failed to update status for message id: $id",
                e,
            )
        }
    }

    @Transactional(readOnly = true)
    override fun findFailedMessagesExceedingRetryCount(
        maxRetries: Int,
        limit: Int,
    ): List<OutboxMessage> =
        try {
            jpaOutboxEventRepository
                .findByStatusAndRetryCountGreaterThanEqual(
                    OutboxMessageStatus.FAILED,
                    maxRetries,
                    PageRequest.of(0, limit),
                ).map { it.toDomain() }
        } catch (e: Exception) {
            logger.error("Failed to find failed messages exceeding retry count", e)
            throw OutboxException.DatabaseOperationException(
                "Failed to find failed messages exceeding retry count",
                e,
            )
        }

    /**
     * Rule 87: 동시성 제어를 위한 데이터베이스 수준 잠금 사용
     * PESSIMISTIC_WRITE 잠금과 함께 SKIP LOCKED 기능 활용
     */
    @Transactional
    override fun findAndMarkForProcessing(
        status: OutboxMessageStatus,
        limit: Int,
    ): List<OutboxMessage> =
        try {
            val pageable: Pageable = PageRequest.of(0, limit)
            val entitiesToProcess =
                when (status) {
                    OutboxMessageStatus.PENDING -> {
                        // Rule 87: SKIP LOCKED를 사용한 동시성 제어
                        jpaOutboxEventRepository.findUnprocessedEventsWithLock(pageable)
                    }
                    else -> {
                        jpaOutboxEventRepository.findTopByStatusOrderByCreatedAtAsc(status, pageable)
                    }
                }

            val processedMessages =
                entitiesToProcess.mapNotNull { entity: OutboxEventEntity ->
                    try {
                        entity.status = OutboxMessageStatus.PROCESSING
                        entity.lastAttemptTime = Instant.now()
                        entity.updatedAt = Instant.now()
                        val savedEntity = jpaOutboxEventRepository.save(entity)
                        savedEntity.toDomain()
                    } catch (e: Exception) {
                        logger.warn("Failed to mark message {} for processing", entity.id, e)
                        null
                    }
                }

            logger.debug("Marked {} messages for processing", processedMessages.size)
            processedMessages
        } catch (e: Exception) {
            logger.error("Failed to find and mark messages for processing", e)
            throw OutboxException.DatabaseOperationException(
                "Failed to find and mark messages for processing",
                e,
            )
        }

    @Transactional(readOnly = true)
    override fun countByStatus(status: OutboxMessageStatus): Long =
        try {
            jpaOutboxEventRepository.countByStatus(status)
        } catch (e: Exception) {
            logger.error("Failed to count messages by status: {}", status, e)
            throw OutboxException.DatabaseOperationException(
                "Failed to count messages by status: $status",
                e,
            )
        }

    @Transactional
    override fun incrementRetryCount(id: Long): OutboxMessage? {
        return try {
            val entity = jpaOutboxEventRepository.findById(id).orElse(null) ?: return null
            entity.retryCount += 1
            entity.lastAttemptTime = Instant.now()
            entity.updatedAt = Instant.now()
            val savedEntity = jpaOutboxEventRepository.save(entity)
            savedEntity.toDomain()
        } catch (e: Exception) {
            logger.error("Failed to increment retry count for message id: {}", id, e)
            throw OutboxException.DatabaseOperationException(
                "Failed to increment retry count for message id: $id",
                e,
            )
        }
    }

    @Transactional(readOnly = true)
    override fun findUnprocessedMessages(batchSize: Int): List<OutboxMessage> =
        try {
            jpaOutboxEventRepository
                .findTopByStatusOrderByCreatedAtAsc(OutboxMessageStatus.PENDING, PageRequest.of(0, batchSize))
                .map { it.toDomain() }
        } catch (e: Exception) {
            logger.error("Failed to find unprocessed messages", e)
            throw OutboxException.DatabaseOperationException(
                "Failed to find unprocessed messages",
                e,
            )
        }

    @Transactional
    override fun updateMessageStatus(
        messageId: Long,
        status: OutboxMessageStatus,
        retryCount: Int,
    ) {
        try {
            val entity =
                jpaOutboxEventRepository.findById(messageId).orElseThrow {
                    OutboxException.MessageNotFoundException(messageId)
                }

            entity.status = status
            entity.retryCount = retryCount
            entity.lastAttemptTime = Instant.now()
            entity.updatedAt = Instant.now()

            jpaOutboxEventRepository.save(entity)
        } catch (e: OutboxException) {
            throw e
        } catch (e: Exception) {
            logger.error("Failed to update message status for id: {}", messageId, e)
            throw OutboxException.DatabaseOperationException(
                "Failed to update message status for id: $messageId",
                e,
            )
        }
    }

    @Transactional(readOnly = true)
    override fun findByStatusAndRetryCountLessThan(
        status: OutboxMessageStatus,
        maxRetries: Int,
        limit: Int,
    ): List<OutboxMessage> =
        try {
            val pageable: Pageable = PageRequest.of(0, limit)
            jpaOutboxEventRepository
                .findByStatusAndRetryCountLessThan(status, maxRetries, pageable)
                .map { it.toDomain() }
        } catch (e: Exception) {
            logger.error("Failed to find messages by status and retry count", e)
            throw OutboxException.DatabaseOperationException(
                "Failed to find messages by status and retry count",
                e,
            )
        }
}
