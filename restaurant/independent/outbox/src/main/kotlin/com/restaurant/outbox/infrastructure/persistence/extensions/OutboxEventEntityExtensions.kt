package com.restaurant.outbox.infrastructure.persistence.extensions

import com.restaurant.outbox.infrastructure.persistence.entity.OutboxEventEntity
import com.restaurant.outbox.port.model.OutboxMessage

// Entity -> Domain
fun OutboxEventEntity.toDomainModel(): OutboxMessage =
    OutboxMessage(
        // OutboxMessage.id는 UUID, OutboxEventEntity.id는 Long?이므로 매핑 불가. 필요시 별도 매핑 정책 적용.
        payload = this.payload,
        topic = this.targetTopic,
        headers = this.headers,
        aggregateId = this.aggregateId,
        aggregateType = this.aggregateType,
        status = this.status,
        retryCount = this.retryCount,
        createdAt = this.createdAt,
        updatedAt = this.updatedAt,
        lastAttemptTime = this.lastAttemptTime,
    )

// Domain -> Entity
fun OutboxMessage.toEntity(): OutboxEventEntity =
    OutboxEventEntity(
        // id(Long?)는 DB에서 생성, eventType은 headers에서 추출
        aggregateType = this.aggregateType,
        aggregateId = this.aggregateId,
        eventType = this.headers["eventType"] ?: "",
        payload = this.payload,
        targetTopic = this.topic,
        headers = this.headers,
        status = this.status,
        retryCount = this.retryCount,
        createdAt = this.createdAt,
        updatedAt = this.updatedAt,
        lastAttemptTime = this.lastAttemptTime,
    )
