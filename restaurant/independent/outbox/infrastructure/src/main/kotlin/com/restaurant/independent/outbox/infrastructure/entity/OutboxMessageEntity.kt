package com.restaurant.independent.outbox.infrastructure.entity

import com.restaurant.independent.outbox.application.port.model.OutboxMessageStatus
import com.restaurant.independent.outbox.infrastructure.converter.StringMapConverter
import jakarta.persistence.Column
import jakarta.persistence.Convert
import jakarta.persistence.Entity
import jakarta.persistence.EnumType
import jakarta.persistence.Enumerated
import jakarta.persistence.Id
import jakarta.persistence.Table
import java.time.Instant
import java.util.UUID

/**
 * Outbox 메시지의 JPA 엔티티.
 */
@Entity
@Table(name = "outbox_messages")
data class OutboxMessageEntity(
    @Id
    val id: UUID,
    @Column(nullable = false)
    val payload: ByteArray,
    @Column(nullable = false)
    val topic: String,
    @Column(nullable = false)
    @Convert(converter = StringMapConverter::class)
    val headers: Map<String, String>,
    @Column(name = "aggregate_id", nullable = false)
    val aggregateId: String,
    @Column(name = "aggregate_type", nullable = false)
    val aggregateType: String,
    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    var status: OutboxMessageStatus,
    @Column(nullable = false)
    var retryCount: Int,
    @Column(nullable = false)
    val createdAt: Instant,
    @Column(nullable = false)
    var updatedAt: Instant,
    @Column(nullable = true)
    var lastAttemptTime: Instant?,
) {
    /**
     * ByteArray 필드가 포함된 엔티티의 equals/hashCode 구현
     */
    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as OutboxMessageEntity

        if (id != other.id) return false
        if (!payload.contentEquals(other.payload)) return false
        if (topic != other.topic) return false
        if (headers != other.headers) return false
        if (aggregateId != other.aggregateId) return false
        if (aggregateType != other.aggregateType) return false
        if (status != other.status) return false
        if (retryCount != other.retryCount) return false
        if (createdAt != other.createdAt) return false
        if (updatedAt != other.updatedAt) return false
        if (lastAttemptTime != other.lastAttemptTime) return false

        return true
    }

    override fun hashCode(): Int {
        var result = id.hashCode()
        result = 31 * result + payload.contentHashCode()
        result = 31 * result + topic.hashCode()
        result = 31 * result + headers.hashCode()
        result = 31 * result + aggregateId.hashCode()
        result = 31 * result + aggregateType.hashCode()
        result = 31 * result + status.hashCode()
        result = 31 * result + retryCount
        result = 31 * result + createdAt.hashCode()
        result = 31 * result + updatedAt.hashCode()
        result = 31 * result + (lastAttemptTime?.hashCode() ?: 0)
        return result
    }
}
