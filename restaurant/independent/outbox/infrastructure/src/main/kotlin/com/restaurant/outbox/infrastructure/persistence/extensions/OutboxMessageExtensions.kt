package com.restaurant.outbox.infrastructure.persistence.extensions

import com.restaurant.outbox.infrastructure.persistence.entity.OutboxEventEntity
import com.restaurant.outbox.infrastructure.persistence.entity.OutboxMessageStatus // Assuming Enum exists here
import com.restaurant.outbox.port.dto.OutboxMessage
import java.time.Instant

/**
 * OutboxMessage DTO (from port) -> OutboxEventEntity (for infrastructure persistence)
 */
fun OutboxMessage.toEntity(): OutboxEventEntity =
    OutboxEventEntity(
        // id = null, // Let JPA generate the ID
        aggregateId = this.aggregateId,
        aggregateType = this.aggregateType,
        eventType = this.eventType,
        payload = this.payload, // Already ByteArray
        targetTopic = this.targetTopic,
        headers = this.headers,
        status = OutboxMessageStatus.PENDING, // Initial status
        retryCount = 0, // Initial retry count
        createdAt = Instant.now(),
        lastAttemptTime = null,
        // version = 0 // If using @Version for optimistic locking
    )

/**
 * OutboxEventEntity -> OutboxMessage DTO (Optional - if needed by poller/sender)
 */
fun OutboxEventEntity.toDto(): OutboxMessage =
    OutboxMessage(
        aggregateId = this.aggregateId,
        aggregateType = this.aggregateType,
        eventType = this.eventType,
        payload = this.payload,
        targetTopic = this.targetTopic,
        headers = this.headers,
        // Note: DTO usually doesn't need status, retryCount, timestamps etc.
    )
