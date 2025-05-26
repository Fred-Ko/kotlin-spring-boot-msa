package com.restaurant.outbox.application.dto

import java.time.Instant

/**
 * Outbox 메시지를 나타내는 도메인 모델
 */
data class OutboxMessage(
    val id: Long? = null,
    val aggregateId: String,
    val aggregateType: String,
    val eventType: String,
    val payload: ByteArray,
    val topic: String,
    val headers: Map<String, String>,
    val status: OutboxMessageStatus = OutboxMessageStatus.PENDING,
    val retryCount: Int = 0,
    val lastAttemptTime: Instant? = null,
    val createdAt: Instant = Instant.now(),
    val updatedAt: Instant? = null,
) {
    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as OutboxMessage

        if (id != other.id) return false
        if (aggregateId != other.aggregateId) return false
        if (aggregateType != other.aggregateType) return false
        if (eventType != other.eventType) return false
        if (!payload.contentEquals(other.payload)) return false
        if (topic != other.topic) return false
        if (headers != other.headers) return false
        if (status != other.status) return false
        if (retryCount != other.retryCount) return false
        if (lastAttemptTime != other.lastAttemptTime) return false
        if (createdAt != other.createdAt) return false
        if (updatedAt != other.updatedAt) return false

        return true
    }

    override fun hashCode(): Int {
        var result = id?.hashCode() ?: 0
        result = 31 * result + aggregateId.hashCode()
        result = 31 * result + aggregateType.hashCode()
        result = 31 * result + eventType.hashCode()
        result = 31 * result + payload.contentHashCode()
        result = 31 * result + topic.hashCode()
        result = 31 * result + headers.hashCode()
        result = 31 * result + status.hashCode()
        result = 31 * result + retryCount
        result = 31 * result + (lastAttemptTime?.hashCode() ?: 0)
        result = 31 * result + createdAt.hashCode()
        result = 31 * result + (updatedAt?.hashCode() ?: 0)
        return result
    }
}
