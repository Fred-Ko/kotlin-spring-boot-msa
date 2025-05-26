package com.restaurant.outbox.infrastructure.persistence.extensions

import com.restaurant.outbox.application.dto.model.OutboxMessage
import com.restaurant.outbox.infrastructure.entity.OutboxEventEntity

fun OutboxEventEntity.toDomain(): OutboxMessage =
    OutboxMessage(
        id = this.id, // dbId 대신 id 사용, nullable이므로 그대로 전달
        aggregateId = this.aggregateId,
        aggregateType = this.aggregateType,
        eventType = this.eventType, // eventType 추가
        payload = this.payload,
        topic = this.topic,
        headers = this.headers,
        status = this.status,
        retryCount = this.retryCount,
        lastAttemptTime = this.lastAttemptTime,
        createdAt = this.createdAt,
        updatedAt = this.updatedAt,
    )

fun OutboxMessage.toEntity(): OutboxEventEntity =
    OutboxEventEntity(
        // id는 DB에서 자동 생성되므로 null
        aggregateId = this.aggregateId,
        aggregateType = this.aggregateType,
        eventType = this.eventType, // eventType 추가
        payload = this.payload,
        topic = this.topic,
        headers = this.headers,
        status = this.status,
        retryCount = this.retryCount,
        lastAttemptTime = this.lastAttemptTime,
        createdAt = this.createdAt,
        updatedAt = this.updatedAt ?: java.time.Instant.now(), // updatedAt이 null이면 현재 시간 사용
    )

fun OutboxMessage.toExistingEntity(existingEntity: OutboxEventEntity): OutboxEventEntity {
    existingEntity.status = this.status
    existingEntity.retryCount = this.retryCount
    existingEntity.lastAttemptTime = this.lastAttemptTime
    existingEntity.updatedAt = this.updatedAt ?: java.time.Instant.now()
    // payload, topic, headers, aggregateId, aggregateType, eventType, createdAt 등은 일반적으로 변경되지 않음
    return existingEntity
}

fun OutboxMessage.updateFromDomain(existingEntity: OutboxEventEntity): OutboxEventEntity = this.toExistingEntity(existingEntity)
