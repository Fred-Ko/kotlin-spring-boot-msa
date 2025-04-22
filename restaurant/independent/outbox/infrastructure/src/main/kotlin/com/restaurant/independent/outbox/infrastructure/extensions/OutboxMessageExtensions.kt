package com.restaurant.independent.outbox.infrastructure.extensions

import com.restaurant.independent.outbox.application.port.model.OutboxMessage
import com.restaurant.independent.outbox.infrastructure.entity.OutboxMessageEntity

/**
 * OutboxMessage 모델과 OutboxMessageEntity 간의 변환을 위한 확장 함수들.
 *
 * - [OutboxMessage.toEntity]: OutboxMessage 도메인 모델을 OutboxMessageEntity로 변환
 * - [OutboxMessageEntity.toDomainModel]: OutboxMessageEntity를 OutboxMessage 도메인 모델로 변환
 */

fun OutboxMessage.toEntity(): OutboxMessageEntity =
    OutboxMessageEntity(
        id = this.id,
        payload = this.payload,
        topic = this.topic,
        headers = this.headers,
        aggregateId = this.aggregateId,
        aggregateType = this.aggregateType,
        status = this.status,
        retryCount = this.retryCount,
        createdAt = this.createdAt,
        updatedAt = this.updatedAt,
        lastAttemptTime = this.lastAttemptTime,
    )

/**
 * Converts an OutboxMessageEntity to an OutboxMessage domain model.
 */
fun OutboxMessageEntity.toDomainModel(): OutboxMessage =
    OutboxMessage(
        id = this.id,
        payload = this.payload,
        topic = this.topic,
        headers = this.headers,
        aggregateId = this.aggregateId,
        aggregateType = this.aggregateType,
        status = this.status,
        retryCount = this.retryCount,
        createdAt = this.createdAt,
        updatedAt = this.updatedAt,
        lastAttemptTime = this.lastAttemptTime,
    )
