package com.restaurant.payment.application.command.dto

import java.math.BigDecimal

/**
 * Command for refunding a payment
 */
data class RefundPaymentCommand(
    val paymentId: String,
    val refundAmount: BigDecimal,
    val reason: String? = null,
)
