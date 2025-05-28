package com.restaurant.outbox.infrastructure.extensions

import com.restaurant.outbox.application.dto.OutboxMessage
import com.restaurant.outbox.infrastructure.entity.OutboxEventEntity

fun OutboxEventEntity.toDomain(): OutboxMessage {
    // DB에서 읽은 JSON 문자열 payload를 OutboxMessage.payload (String 또는 ByteArray)로 전달합니다.
    // OutboxEventEntity.payload는 String이므로, 여기서는 String으로 전달됩니다.
    return OutboxMessage(
        id = this.id,
        aggregateId = this.aggregateId,
        aggregateType = this.aggregateType,
        eventType = this.eventType,
        payload = this.payload, // DB의 String payload를 그대로 전달
        topic = this.topic,
        headers = this.headers,
        status = this.status,
        retryCount = this.retryCount,
        lastAttemptAt = this.lastAttemptTime,
        createdAt = this.createdAt,
        errorMessage = null,
    )
}

fun OutboxMessage.toEntity(): OutboxEventEntity {
    // OutboxMessage.payload (String 또는 ByteArray)를 OutboxEventEntity.payload (String)로 변환합니다.
    val payloadAsString: String =
        when (this.payload) {
            is String -> this.payload
            is ByteArray -> this.payload.toString(Charsets.UTF_8)
            else -> {
                // Rule 81에 따라 OutboxMessage.payload는 String 또는 ByteArray이어야 합니다.
                // 이 외의 타입이 들어오면 로직 또는 설정 오류입니다.
                throw IllegalArgumentException(
                    "OutboxMessage.payload must be a JSON String or ByteArray. Found: ${this.payload::class.simpleName}",
                )
            }
        }

    return OutboxEventEntity(
        aggregateId = this.aggregateId,
        aggregateType = this.aggregateType,
        eventType = this.eventType,
        payload = payloadAsString, // 변환된 String payload
        topic = this.topic,
        headers = this.headers,
        status = this.status,
        retryCount = this.retryCount,
        lastAttemptTime = this.lastAttemptAt,
        createdAt = this.createdAt,
        updatedAt = java.time.Instant.now(),
    )
}

fun OutboxMessage.toExistingEntity(existingEntity: OutboxEventEntity): OutboxEventEntity {
    existingEntity.status = this.status
    existingEntity.retryCount = this.retryCount
    existingEntity.lastAttemptTime = this.lastAttemptAt
    existingEntity.updatedAt = java.time.Instant.now()
    // payload는 일반적으로 변경되지 않음
    return existingEntity
}

fun OutboxMessage.updateFromDomain(existingEntity: OutboxEventEntity): OutboxEventEntity = this.toExistingEntity(existingEntity)
