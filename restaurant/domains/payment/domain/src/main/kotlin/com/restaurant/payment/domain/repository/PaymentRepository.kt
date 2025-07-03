package com.restaurant.payment.domain.repository

import com.restaurant.payment.domain.aggregate.Payment
import com.restaurant.payment.domain.vo.OrderId
import com.restaurant.payment.domain.vo.PaymentId
import com.restaurant.payment.domain.vo.PaymentMethodId
import com.restaurant.payment.domain.vo.PaymentStatus
import com.restaurant.payment.domain.vo.UserId

/**
 * Payment aggregate repository interface (Rule 137)
 * 결제 애그리거트의 저장소 인터페이스
 */
interface PaymentRepository {
    /**
     * 결제를 저장합니다.
     */
    suspend fun save(payment: Payment): Payment

    /**
     * 결제 ID로 결제를 조회합니다.
     */
    suspend fun findById(paymentId: PaymentId): Payment?

    /**
     * 주문 ID로 결제를 조회합니다.
     */
    suspend fun findByOrderId(orderId: OrderId): Payment?

    /**
     * 사용자 ID로 결제 목록을 조회합니다.
     */
    suspend fun findByUserId(userId: UserId): List<Payment>

    /**
     * 사용자 ID와 페이지네이션으로 결제 목록을 조회합니다.
     */
    suspend fun findByUserIdWithPagination(
        userId: UserId,
        offset: Int,
        limit: Int,
    ): List<Payment>

    /**
     * 결제를 삭제합니다.
     */
    suspend fun delete(paymentId: PaymentId)

    /**
     * 결제 존재 여부를 확인합니다.
     */
    suspend fun existsById(paymentId: PaymentId): Boolean

    /**
     * 주문 ID로 결제 존재 여부를 확인합니다.
     */
    suspend fun existsByOrderId(orderId: OrderId): Boolean
    
    /**
     * 특정 결제 수단으로 진행 중인 결제들을 조회합니다.
     */
    suspend fun findByPaymentMethodIdAndStatus(
        paymentMethodId: PaymentMethodId, 
        statuses: List<PaymentStatus>
    ): List<Payment>
    
    /**
     * 특정 결제 수단을 사용한 모든 결제를 조회합니다.
     */
    suspend fun findByPaymentMethodId(paymentMethodId: PaymentMethodId): List<Payment>
}
