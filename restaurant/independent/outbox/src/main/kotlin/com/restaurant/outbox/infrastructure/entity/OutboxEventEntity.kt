package com.restaurant.outbox.infrastructure.persistence.entity

import com.restaurant.outbox.infrastructure.converter.StringMapConverter
import com.restaurant.outbox.port.model.OutboxMessageStatus
import jakarta.persistence.Column
import jakarta.persistence.Convert
import jakarta.persistence.Entity
import jakarta.persistence.EnumType
import jakarta.persistence.Enumerated
import jakarta.persistence.GeneratedValue
import jakarta.persistence.GenerationType
import jakarta.persistence.Id
import jakarta.persistence.Lob
import jakarta.persistence.Table
import jakarta.persistence.Version
import java.time.Instant

/**
 * JPA Entity for Outbox messages.
 * Rule 83
 */
@Entity
@Table(name = "outbox_events")
data class OutboxEventEntity(
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    val id: Long? = null,
    @Column(name = "aggregate_type", nullable = false)
    val aggregateType: String,
    @Column(name = "aggregate_id", nullable = false)
    val aggregateId: String,
    @Column(name = "event_type", nullable = false)
    val eventType: String,
    @Lob
    @Column(name = "payload", nullable = false)
    val payload: ByteArray,
    @Column(name = "target_topic", nullable = false)
    val targetTopic: String,
    @Lob
    @Column(name = "headers", nullable = false, columnDefinition = "TEXT")
    @Convert(converter = StringMapConverter::class)
    val headers: Map<String, String>,
    @Enumerated(EnumType.STRING)
    @Column(name = "status", nullable = false)
    var status: OutboxMessageStatus,
    @Column(nullable = false)
    var retryCount: Int,
    @Column(name = "created_at", nullable = false, updatable = false)
    val createdAt: Instant,
    @Column(nullable = false)
    var updatedAt: Instant,
    @Column(nullable = true)
    var lastAttemptTime: Instant?,
    @Version
    @Column(nullable = false)
    val version: Long = 0L,
) {
    /**
     * ByteArray 필드가 포함된 엔티티의 equals/hashCode 구현
     */
    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as OutboxEventEntity

        if (id != other.id) return false
        if (!payload.contentEquals(other.payload)) return false
        if (targetTopic != other.targetTopic) return false
        if (headers != other.headers) return false
        if (aggregateId != other.aggregateId) return false
        if (aggregateType != other.aggregateType) return false
        if (status != other.status) return false
        if (retryCount != other.retryCount) return false
        if (createdAt != other.createdAt) return false
        if (updatedAt != other.updatedAt) return false
        if (lastAttemptTime != other.lastAttemptTime) return false
        if (version != other.version) return false

        return true
    }

    override fun hashCode(): Int {
        var result = id.hashCode()
        result = 31 * result + payload.contentHashCode()
        result = 31 * result + targetTopic.hashCode()
        result = 31 * result + headers.hashCode()
        result = 31 * result + aggregateId.hashCode()
        result = 31 * result + aggregateType.hashCode()
        result = 31 * result + status.hashCode()
        result = 31 * result + retryCount
        result = 31 * result + createdAt.hashCode()
        result = 31 * result + updatedAt.hashCode()
        result = 31 * result + (lastAttemptTime?.hashCode() ?: 0)
        result = 31 * result + version.hashCode()
        return result
    }
}
