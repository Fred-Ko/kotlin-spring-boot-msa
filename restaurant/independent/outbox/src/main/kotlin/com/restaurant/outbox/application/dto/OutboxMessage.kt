package com.restaurant.outbox.application.dto

import java.time.Instant

/**
 * Outbox 메시지를 나타내는 도메인 모델
 */
data class OutboxMessage(
    val id: Long? = null,
    val payload: Any,
    val topic: String,
    val headers: Map<String, String>,
    val aggregateType: String,
    val aggregateId: String,
    val eventType: String,
    val createdAt: Instant = Instant.now(),
    val status: OutboxMessageStatus = OutboxMessageStatus.PENDING,
    val retryCount: Int = 0,
    val lastAttemptAt: Instant? = null,
    val errorMessage: String? = null,
) {
    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as OutboxMessage

        if (id != other.id) return false
        if (payload != other.payload) return false
        if (topic != other.topic) return false
        if (headers != other.headers) return false
        if (aggregateType != other.aggregateType) return false
        if (aggregateId != other.aggregateId) return false
        if (eventType != other.eventType) return false
        if (createdAt != other.createdAt) return false
        if (status != other.status) return false
        if (retryCount != other.retryCount) return false
        if (lastAttemptAt != other.lastAttemptAt) return false
        if (errorMessage != other.errorMessage) return false

        return true
    }

    override fun hashCode(): Int {
        var result = id?.hashCode() ?: 0
        result = 31 * result + payload.hashCode()
        result = 31 * result + topic.hashCode()
        result = 31 * result + headers.hashCode()
        result = 31 * result + aggregateType.hashCode()
        result = 31 * result + aggregateId.hashCode()
        result = 31 * result + eventType.hashCode()
        result = 31 * result + createdAt.hashCode()
        result = 31 * result + status.hashCode()
        result = 31 * result + retryCount
        result = 31 * result + (lastAttemptAt?.hashCode() ?: 0)
        result = 31 * result + (errorMessage?.hashCode() ?: 0)
        return result
    }
}
