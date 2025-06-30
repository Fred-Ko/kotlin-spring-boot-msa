package com.restaurant.payment.infrastructure.entity

import com.restaurant.common.infrastructure.entity.BaseEntity
import com.restaurant.payment.domain.vo.PaymentStatus
import jakarta.persistence.Column
import jakarta.persistence.Entity
import jakarta.persistence.EnumType
import jakarta.persistence.Enumerated
import jakarta.persistence.Table
import java.math.BigDecimal
import java.time.Instant
import java.util.UUID

/**
 * Payment JPA Entity (Rule 19-31)
 * Infrastructure 레이어에서 영속화 구조를 반영하며, 도메인 로직을 포함하지 않습니다.
 */
@Entity
@Table(name = "payments")
class PaymentEntity(
    @Column(nullable = false, unique = true)
    val domainId: UUID,
    @Column(nullable = false)
    val orderId: UUID,
    @Column(nullable = false)
    val userId: UUID,
    @Column(nullable = false, precision = 10, scale = 2)
    val amount: BigDecimal,
    @Column(nullable = false)
    val paymentMethodId: UUID,
    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    val status: PaymentStatus,
    val transactionId: String?,
    val failureMessage: String?,
    @Column(nullable = false)
    val requestedAt: Instant,
    val completedAt: Instant?,
) : BaseEntity()
