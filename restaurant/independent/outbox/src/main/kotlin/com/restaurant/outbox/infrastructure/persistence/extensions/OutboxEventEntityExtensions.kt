package com.restaurant.outbox.infrastructure.persistence.extensions

import com.restaurant.outbox.application.port.model.OutboxMessage
import com.restaurant.outbox.infrastructure.entity.OutboxEventEntity

// Entity -> Domain
fun OutboxEventEntity.toOutboxMessage(): OutboxMessage =
    OutboxMessage(
        dbId = id,
        payload = payload,
        topic = topic,
        headers = headers,
        aggregateId = aggregateId,
        aggregateType = aggregateType,
        status = status,
        retryCount = retryCount,
        createdAt = createdAt,
        lastAttemptTime = lastAttemptTime,
    )

// Domain -> Entity
fun OutboxMessage.toOutboxEventEntity(): OutboxEventEntity =
    OutboxEventEntity(
        aggregateType = aggregateType,
        aggregateId = aggregateId,
        topic = topic,
        payload = payload,
        headers = headers,
        status = status,
        retryCount = retryCount,
        createdAt = createdAt,
        lastAttemptTime = lastAttemptTime,
    )
