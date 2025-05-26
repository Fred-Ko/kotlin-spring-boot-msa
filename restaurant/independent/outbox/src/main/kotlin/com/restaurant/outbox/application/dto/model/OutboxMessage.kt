package com.restaurant.outbox.application.dto.model

import java.time.Instant

/** Outbox에 저장되는 메시지 데이터 모델 */
data class OutboxMessage(
    val id: Long? = null,
    val payload: ByteArray,
    val topic: String,
    val headers: Map<String, String>,
    val aggregateType: String,
    val aggregateId: String,
    val eventType: String,
    val status: OutboxMessageStatus = OutboxMessageStatus.PENDING,
    val retryCount: Int = 0,
    val createdAt: Instant = Instant.now(),
    val updatedAt: Instant? = Instant.now(), // 타입을 Instant? 로 변경
    val lastAttemptTime: Instant? = null,
) {
    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as OutboxMessage

        if (id != other.id) return false
        if (!payload.contentEquals(other.payload)) return false
        if (topic != other.topic) return false
        if (headers != other.headers) return false
        if (aggregateType != other.aggregateType) return false
        if (aggregateId != other.aggregateId) return false
        if (eventType != other.eventType) return false
        if (status != other.status) return false
        if (retryCount != other.retryCount) return false
        if (createdAt != other.createdAt) return false
        if (updatedAt != other.updatedAt) return false
        if (lastAttemptTime != other.lastAttemptTime) return false

        return true
    }

    override fun hashCode(): Int {
        var result = id?.hashCode() ?: 0
        result = 31 * result + payload.contentHashCode()
        result = 31 * result + topic.hashCode()
        result = 31 * result + headers.hashCode()
        result = 31 * result + aggregateType.hashCode()
        result = 31 * result + aggregateId.hashCode()
        result = 31 * result + eventType.hashCode()
        result = 31 * result + status.hashCode()
        result = 31 * result + retryCount
        result = 31 * result + createdAt.hashCode()
        result = 31 * result + (updatedAt?.hashCode() ?: 0) // nullable 처리
        result = 31 * result + (lastAttemptTime?.hashCode() ?: 0)
        return result
    }

    override fun toString(): String =
        "OutboxMessage(" +
            "id=$id, " +
            "topic='$topic', " +
            "headers=$headers, " +
            "aggregateId='$aggregateId', " +
            "aggregateType='$aggregateType', " +
            "eventType='$eventType', " +
            "status=$status, " +
            "retryCount=$retryCount, " +
            "createdAt=$createdAt, " +
            "updatedAt=$updatedAt, " +
            "lastAttemptTime=$lastAttemptTime" +
            ")"
}
