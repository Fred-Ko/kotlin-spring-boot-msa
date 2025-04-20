package com.restaurant.shared.outbox.infrastructure.entity

import jakarta.persistence.Column
import jakarta.persistence.Entity
import jakarta.persistence.GeneratedValue
import jakarta.persistence.GenerationType
import jakarta.persistence.Id
import jakarta.persistence.Lob
import jakarta.persistence.Table
import jakarta.persistence.Version
import java.time.LocalDateTime
import java.util.UUID

@Entity
@Table(name = "outbox_events") // Rule 22 variant for outbox
class OutboxEventEntity(
    // JPA PK
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    val id: Long? = null,
    // Outbox event unique ID
    @Column(nullable = false, updatable = false)
    val eventId: UUID,
    // Aggregate type
    @Column(nullable = false, updatable = false)
    val aggregateType: String,
    // Aggregate ID
    @Column(nullable = false, updatable = false)
    val aggregateId: String,
    // Event type
    @Column(nullable = false, updatable = false)
    val eventType: String,
    // Payload (large)
    @Lob
    @Column(nullable = false, updatable = false, columnDefinition = "TEXT")
    val payload: String,
    // Status (변경됨)
    @Column(nullable = false)
    var status: String,
    // 발생 시각
    @Column(nullable = false, updatable = false)
    val occurredAt: LocalDateTime,
    // 처리 시각 (변경됨)
    @Column(nullable = true)
    var processedAt: LocalDateTime? = null,
    // 재시도 횟수 (변경됨)
    @Column(nullable = false)
    var retryCount: Int = 0,
    // 마지막 시도 시각 (변경됨)
    @Column(nullable = true)
    var lastAttemptTime: LocalDateTime? = null,
    // 에러 메시지 (변경됨)
    @Lob
    @Column(nullable = true, columnDefinition = "TEXT")
    var errorMessage: String? = null,
    // 버전
    @Version
    @Column(nullable = false)
    val version: Long = 0,
) {
    // Companion object for constants if needed (e.g., status strings)
    companion object {
        const val STATUS_PENDING = "PENDING"
        const val STATUS_PROCESSING = "PROCESSING"
        const val STATUS_SENT = "SENT"
        const val STATUS_FAILED = "FAILED"
    }
}
