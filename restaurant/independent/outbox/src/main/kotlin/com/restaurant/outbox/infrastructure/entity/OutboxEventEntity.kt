package com.restaurant.outbox.infrastructure.entity

import com.restaurant.outbox.application.port.model.OutboxMessageStatus
import com.restaurant.outbox.infrastructure.persistence.converter.StringMapConverter
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
class OutboxEventEntity(
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    val id: Long? = null,
    @Column(name = "aggregate_type", nullable = false)
    val aggregateType: String,
    @Column(name = "aggregate_id", nullable = false)
    val aggregateId: String,
    @Column(nullable = false)
    val topic: String,
    @Lob
    @Column(nullable = false)
    val payload: ByteArray,
    @Convert(converter = StringMapConverter::class)
    @Column(nullable = false, columnDefinition = "text")
    val headers: Map<String, String>,
    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    var status: OutboxMessageStatus = OutboxMessageStatus.PENDING,
    @Column(nullable = false)
    val createdAt: Instant = Instant.now(),
    @Column(nullable = true)
    var lastAttemptTime: Instant? = null,
    @Column(nullable = false)
    var retryCount: Int = 0,
    @Version
    val version: Long = 0,
) {
    /**
     * ByteArray 필드가 포함된 엔티티의 equals/hashCode 구현
     */
    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as OutboxEventEntity

        if (id != other.id) return false
        if (aggregateType != other.aggregateType) return false
        if (aggregateId != other.aggregateId) return false
        if (topic != other.topic) return false
        if (!payload.contentEquals(other.payload)) return false
        if (headers != other.headers) return false
        if (status != other.status) return false
        if (createdAt != other.createdAt) return false
        if (lastAttemptTime != other.lastAttemptTime) return false
        if (retryCount != other.retryCount) return false
        if (version != other.version) return false

        return true
    }

    override fun hashCode(): Int {
        var result = id?.hashCode() ?: 0
        result = 31 * result + aggregateType.hashCode()
        result = 31 * result + aggregateId.hashCode()
        result = 31 * result + topic.hashCode()
        result = 31 * result + payload.contentHashCode()
        result = 31 * result + headers.hashCode()
        result = 31 * result + status.hashCode()
        result = 31 * result + createdAt.hashCode()
        result = 31 * result + (lastAttemptTime?.hashCode() ?: 0)
        result = 31 * result + retryCount
        result = 31 * result + version.hashCode()
        return result
    }
}
