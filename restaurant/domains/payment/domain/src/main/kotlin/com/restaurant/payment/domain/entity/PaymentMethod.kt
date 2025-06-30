package com.restaurant.payment.domain.entity

import com.restaurant.payment.domain.vo.PaymentMethodId
import com.restaurant.payment.domain.vo.PaymentMethodType
import com.restaurant.payment.domain.vo.UserId
import java.time.Instant

/**
 * PaymentMethod Domain Entity (Rule 11)
 * 사용자의 결제 수단 정보를 담당하는 Domain Entity.
 * sealed interface로 정의하여 결제 수단 종류에 따른 다형성을 지원합니다.
 */
sealed interface PaymentMethod {
    val paymentMethodId: PaymentMethodId
    val userId: UserId
    val type: PaymentMethodType
    val alias: String
    val isDefault: Boolean
    val createdAt: Instant
    val updatedAt: Instant
    val version: Long

    fun updateAlias(newAlias: String): PaymentMethod

    fun setAsDefault(): PaymentMethod

    fun unsetAsDefault(): PaymentMethod
}
