package com.restaurant.outbox.infrastructure.persistence.extensions

import com.restaurant.outbox.infrastructure.persistence.entity.OutboxEventEntity
import com.restaurant.outbox.port.model.OutboxMessage

/**
 * OutboxMessage DTO (from port) -> OutboxEventEntity (for infrastructure persistence)
 */
fun OutboxMessage.toEntity(): OutboxEventEntity =
    OutboxEventEntity(
        aggregateId = this.aggregateId,
        aggregateType = this.aggregateType,
        eventType = this.headers["eventType"] ?: "",
        payload = this.payload,
        targetTopic = this.topic,
        headers = this.headers,
        status = this.status,
        retryCount = this.retryCount,
        createdAt = this.createdAt,
        lastAttemptTime = this.lastAttemptTime,
        updatedAt = this.updatedAt,
    )

/**
 * OutboxEventEntity -> OutboxMessage DTO (Optional - if needed by poller/sender)
 */
fun OutboxEventEntity.toDto(): OutboxMessage =
    OutboxMessage(
        aggregateId = this.aggregateId,
        aggregateType = this.aggregateType,
        payload = this.payload,
        topic = this.targetTopic,
        headers = this.headers,
        status = this.status,
        retryCount = this.retryCount,
        createdAt = this.createdAt,
        updatedAt = this.updatedAt,
        lastAttemptTime = this.lastAttemptTime,
    )

fun OutboxEventEntity.toDomainModel(): OutboxMessage =
    OutboxMessage(
        aggregateId = this.aggregateId,
        aggregateType = this.aggregateType,
        payload = this.payload,
        topic = this.targetTopic,
        headers = this.headers,
        status = this.status,
        retryCount = this.retryCount,
        createdAt = this.createdAt,
        updatedAt = this.updatedAt,
        lastAttemptTime = this.lastAttemptTime,
    )
