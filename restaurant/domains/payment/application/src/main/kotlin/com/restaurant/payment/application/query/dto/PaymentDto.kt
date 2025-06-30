package com.restaurant.payment.application.query.dto

import java.math.BigDecimal
import java.time.Instant

/**
 * DTO representing payment information
 */
data class PaymentDto(
    val id: String,
    val orderId: String,
    val userId: String,
    val paymentMethodId: String,
    val amount: BigDecimal,
    val status: String,
    val transactionId: String?,
    val failureReason: String?,
    val refundedAmount: BigDecimal?,
    val paymentMethods: List<PaymentMethodDto>,
    val createdAt: Instant,
    val updatedAt: Instant,
)

/**
 * DTO representing payment method information
 */
data class PaymentMethodDto(
    val id: String,
    val userId: String,
    val type: String,
    val alias: String,
    val maskedCardNumber: String?,
    val cardType: String?,
    val isDefault: Boolean,
    val isExpired: Boolean,
    val createdAt: Instant,
    val updatedAt: Instant,
)
