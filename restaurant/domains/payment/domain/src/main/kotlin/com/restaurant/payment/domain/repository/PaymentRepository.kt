package com.restaurant.payment.domain.repository

import com.restaurant.payment.domain.aggregate.Payment
import com.restaurant.payment.domain.vo.OrderId
import com.restaurant.payment.domain.vo.PaymentId
import com.restaurant.payment.domain.vo.PaymentStatus
import com.restaurant.payment.domain.vo.UserId

/**
 * Payment repository interface (Rule 137)
 * 결제 애그리거트의 저장소 인터페이스
 */
interface PaymentRepository {
    /**
     * 결제를 저장합니다.
     */
    fun save(payment: Payment): Payment

    /**
     * 결제 ID로 결제를 조회합니다.
     */
    fun findById(paymentId: PaymentId): Payment?

    /**
     * 주문 ID로 결제를 조회합니다.
     */
    fun findByOrderId(orderId: OrderId): Payment?

    /**
     * 사용자 ID로 결제 목록을 조회합니다.
     */
    fun findByUserId(userId: UserId): List<Payment>

    /**
     * 결제 상태로 결제 목록을 조회합니다.
     */
    fun findByStatus(status: PaymentStatus): List<Payment>

    /**
     * 사용자 ID와 결제 상태로 결제 목록을 조회합니다.
     */
    fun findByUserIdAndStatus(
        userId: UserId,
        status: PaymentStatus,
    ): List<Payment>

    /**
     * 결제를 삭제합니다.
     */
    fun delete(payment: Payment)

    /**
     * 결제 ID로 결제를 삭제합니다.
     */
    fun deleteById(paymentId: PaymentId)
}
