package com.restaurant.payment.application.command.dto

/**
 * Command for deleting a payment method
 */
data class DeletePaymentMethodCommand(
    val paymentMethodId: String,
    val userId: String,
)
