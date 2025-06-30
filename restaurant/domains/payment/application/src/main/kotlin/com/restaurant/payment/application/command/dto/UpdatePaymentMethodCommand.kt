package com.restaurant.payment.application.command.dto

/**
 * Command for updating a payment method
 */
data class UpdatePaymentMethodCommand(
    val paymentMethodId: String,
    val cardExpiry: String? = null,
    val alias: String? = null,
    val isDefault: Boolean? = null,
    val isActive: Boolean? = null,
)
