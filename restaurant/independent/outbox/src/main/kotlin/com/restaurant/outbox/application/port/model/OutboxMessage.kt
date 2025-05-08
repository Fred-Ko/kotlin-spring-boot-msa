package com.restaurant.outbox.application.port.model

import java.time.Instant
import java.util.UUID

/**
 * Represents a message to be stored in the outbox.
 * This is a technology-agnostic model that contains all necessary information for message delivery.
 *
 * @property id Unique identifier for the message
 * @property dbId Database ID of the message
 * @property payload The serialized message content as a byte array
 * @property topic The target Kafka topic for message delivery
 * @property headers Additional message headers including correlationId, aggregateType, aggregateId, etc.
 * @property aggregateId Domain ID of the aggregate that generated the event (in string format)
 * @property aggregateType Type of the aggregate that generated the event
 * @property status Current status of the message
 * @property retryCount Number of retry attempts made
 * @property createdAt When the message was created
 * @property updatedAt When the message was last updated
 * @property lastAttemptTime When the last delivery attempt was made
 */
data class OutboxMessage(
    val id: UUID = UUID.randomUUID(),
    val dbId: Long? = null,
    val payload: ByteArray,
    val topic: String,
    val headers: Map<String, String>,
    val aggregateType: String,
    val aggregateId: String,
    val status: OutboxMessageStatus = OutboxMessageStatus.PENDING,
    val retryCount: Int = 0,
    val createdAt: Instant = Instant.now(),
    val updatedAt: Instant = Instant.now(),
    val lastAttemptTime: Instant? = null,
) {
    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as OutboxMessage

        if (id != other.id) return false
        if (dbId != other.dbId) return false
        if (!payload.contentEquals(other.payload)) return false
        if (topic != other.topic) return false
        if (headers != other.headers) return false
        if (aggregateType != other.aggregateType) return false
        if (aggregateId != other.aggregateId) return false
        if (status != other.status) return false
        if (retryCount != other.retryCount) return false
        if (createdAt != other.createdAt) return false
        if (updatedAt != other.updatedAt) return false
        if (lastAttemptTime != other.lastAttemptTime) return false

        return true
    }

    override fun hashCode(): Int {
        var result = id.hashCode()
        result = 31 * result + (dbId?.hashCode() ?: 0)
        result = 31 * result + payload.contentHashCode()
        result = 31 * result + topic.hashCode()
        result = 31 * result + headers.hashCode()
        result = 31 * result + aggregateType.hashCode()
        result = 31 * result + aggregateId.hashCode()
        result = 31 * result + status.hashCode()
        result = 31 * result + retryCount
        result = 31 * result + createdAt.hashCode()
        result = 31 * result + updatedAt.hashCode()
        result = 31 * result + (lastAttemptTime?.hashCode() ?: 0)
        return result
    }

    override fun toString(): String =
        "OutboxMessage(" +
            "id=$id, " +
            "dbId=$dbId, " +
            "topic='$topic', " +
            "headers=$headers, " +
            "aggregateId='$aggregateId', " +
            "aggregateType='$aggregateType', " +
            "status=$status, " +
            "retryCount=$retryCount, " +
            "createdAt=$createdAt, " +
            "updatedAt=$updatedAt, " +
            "lastAttemptTime=$lastAttemptTime" +
            ")"
}
