package com.restaurant.payment.presentation.v1.query.extensions.dto.response

import com.restaurant.payment.application.query.dto.PaymentDto
import com.restaurant.payment.application.query.dto.PaymentMethodDto
import com.restaurant.payment.presentation.v1.query.dto.response.PaymentMethodResponseV1
import com.restaurant.payment.presentation.v1.query.dto.response.PaymentResponseV1

/**
 * Payment Query Response DTO Extensions (Rule 5, 7, 58, 59)
 * Application Query Result -> Presentation Response DTO conversion
 */

/**
 * PaymentDto -> PaymentResponseV1 변환
 */
fun PaymentDto.toResponseV1(): PaymentResponseV1 {
    return PaymentResponseV1(
        paymentId = this.id,
        orderId = this.orderId,
        amount = this.amount,
        currency = "KRW", // 기본값
        status = this.status,
        paymentMethodId = this.paymentMethodId,
        description = null, // PaymentDto에 description 필드가 없음
        refundedAmount = this.refundedAmount,
        createdAt = this.createdAt,
        updatedAt = this.updatedAt,
    )
}

/**
 * PaymentMethodDto -> PaymentMethodResponseV1 변환
 */
fun PaymentMethodDto.toResponseV1(): PaymentMethodResponseV1 {
    return PaymentMethodResponseV1(
        paymentMethodId = this.id,
        userId = this.userId,
        type = this.type,
        maskedCardNumber = this.maskedCardNumber ?: "****-****-****-0000",
        cardExpiry = "12/25", // PaymentMethodDto에 cardExpiry 필드가 없음
        alias = this.alias,
        isDefault = this.isDefault,
        isActive = !this.isExpired, // isExpired의 반대값 사용
        createdAt = this.createdAt,
        updatedAt = this.updatedAt,
    )
} 