package com.restaurant.independent.outbox.infrastructure.entity

import jakarta.persistence.*
import java.time.Instant

/**
 * Outbox 이벤트 엔티티
 */
@Entity
@Table(name = "outbox_events")
class OutboxEventEntity(
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    val id: Long? = null,
    @Column(nullable = false)
    val payload: ByteArray,
    @Column(nullable = false)
    val eventType: String,
    @Column(nullable = false)
    val topic: String,
    @Column(nullable = false)
    val aggregateType: String,
    @Column(nullable = false)
    val aggregateId: String,
    @Column(nullable = false)
    val correlationId: String,
    @Column(nullable = false)
    @Enumerated(EnumType.STRING)
    var status: OutboxEventStatus = OutboxEventStatus.PENDING,
    @Column(nullable = false)
    val createdAt: Instant = Instant.now(),
    @Column(nullable = true)
    var lastAttemptTime: Instant? = null,
    @Column(nullable = false)
    var retryCount: Int = 0,
    @Version
    var version: Long = 0,
) {
    enum class OutboxEventStatus {
        PENDING,
        PROCESSING,
        SENT,
        FAILED,
    }

    fun incrementRetryCount() {
        retryCount++
        lastAttemptTime = Instant.now()
    }

    fun markAsProcessing() {
        status = OutboxEventStatus.PROCESSING
        lastAttemptTime = Instant.now()
    }

    fun markAsSent() {
        status = OutboxEventStatus.SENT
        lastAttemptTime = Instant.now()
    }

    fun markAsFailed() {
        status = OutboxEventStatus.FAILED
        lastAttemptTime = Instant.now()
    }
}
