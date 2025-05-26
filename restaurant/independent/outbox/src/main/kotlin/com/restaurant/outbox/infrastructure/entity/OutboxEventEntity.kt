package com.restaurant.outbox.infrastructure.entity

import com.restaurant.outbox.application.dto.OutboxMessageStatus
import com.restaurant.outbox.infrastructure.converter.StringMapConverter
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

/** Outbox 메시지 JPA Entity */
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
    @Column(name = "event_type", nullable = false)
    val eventType: String,
    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    var status: OutboxMessageStatus = OutboxMessageStatus.PENDING,
    @Column(name = "created_at", nullable = false, updatable = false)
    val createdAt: Instant = Instant.now(),
    @Column(name = "updated_at")
    var updatedAt: Instant? = Instant.now(), // 추가된 필드, 생성 시 현재 시간으로 초기화
    @Column(name = "last_attempt_time")
    var lastAttemptTime: Instant? = null,
    @Column(name = "retry_count", nullable = false)
    var retryCount: Int = 0,
    @Version
    var version: Long = 0,
) {
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
        if (updatedAt != other.updatedAt) return false // 추가된 필드 비교
        if (lastAttemptTime != other.lastAttemptTime) return false
        if (retryCount != other.retryCount) return false
        if (eventType != other.eventType) return false
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
        result = 31 * result + (updatedAt?.hashCode() ?: 0) // 추가된 필드 해시코드
        result = 31 * result + (lastAttemptTime?.hashCode() ?: 0)
        result = 31 * result + retryCount
        result = 31 * result + eventType.hashCode()
        result = 31 * result + version.hashCode()
        return result
    }
}
