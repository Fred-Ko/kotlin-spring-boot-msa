package com.restaurant.payment.application.command.dto

import java.math.BigDecimal

/**
 * Command for processing a payment
 */
data class ProcessPaymentCommand(
    val orderId: String,
    val userId: String,
    val paymentMethodId: String,
    val amount: BigDecimal,
    val description: String? = null,
)
