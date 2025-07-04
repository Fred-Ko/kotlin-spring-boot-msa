package com.restaurant.payment.domain.repository

import com.restaurant.payment.domain.aggregate.PaymentMethod
import com.restaurant.payment.domain.vo.PaymentMethodId
import com.restaurant.payment.domain.vo.UserId

/**
 * PaymentMethod repository interface (Rule 137)
 * 결제 수단 애그리거트의 저장소 인터페이스
 */
interface PaymentMethodRepository {
    /**
     * 결제 수단을 저장합니다.
     */
    fun save(paymentMethod: PaymentMethod): PaymentMethod

    /**
     * 결제 수단 ID로 결제 수단을 조회합니다.
     */
    fun findById(paymentMethodId: PaymentMethodId): PaymentMethod?

    /**
     * 사용자 ID로 결제 수단 목록을 조회합니다.
     */
    fun findByUserId(userId: UserId): List<PaymentMethod>

    /**
     * 사용자 ID로 기본 결제 수단을 조회합니다.
     */
    fun findDefaultByUserId(userId: UserId): PaymentMethod?

    /**
     * 결제 수단을 삭제합니다.
     */
    fun delete(paymentMethod: PaymentMethod)

    /**
     * 결제 수단 ID로 결제 수단을 삭제합니다.
     */
    fun deleteById(paymentMethodId: PaymentMethodId)

    /**
     * 결제 수단 존재 여부를 확인합니다.
     */
    fun existsById(paymentMethodId: PaymentMethodId): Boolean
}
