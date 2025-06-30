package com.restaurant.payment.presentation.v1.command.extensions.dto.request

import com.restaurant.payment.application.command.dto.ProcessPaymentCommand
import com.restaurant.payment.application.command.dto.RefundPaymentCommand
import com.restaurant.payment.application.command.dto.RegisterPaymentMethodCommand
import com.restaurant.payment.application.command.dto.UpdatePaymentMethodCommand
import com.restaurant.payment.presentation.v1.command.dto.request.ProcessPaymentRequestV1
import com.restaurant.payment.presentation.v1.command.dto.request.RefundPaymentRequestV1
import com.restaurant.payment.presentation.v1.command.dto.request.RegisterPaymentMethodRequestV1
import com.restaurant.payment.presentation.v1.command.dto.request.UpdatePaymentMethodRequestV1
import java.math.BigDecimal


/**
 * Payment Command Request DTO Extensions (Rule 5, 7, 58, 59)
 * Presentation Request -> Application Command DTO conversion
 */

/**
 * ProcessPaymentRequestV1 -> ProcessPaymentCommand 변환
 */
fun ProcessPaymentRequestV1.toCommand(orderId: String, userId: String): ProcessPaymentCommand {
    return ProcessPaymentCommand(
        orderId = orderId,
        userId = userId,
        paymentMethodId = this.paymentMethodId,
        amount = this.amount,
        description = this.description,
    )
}

/**
 * RefundPaymentRequestV1 -> RefundPaymentCommand 변환
 */
fun RefundPaymentRequestV1.toCommand(paymentId: String): RefundPaymentCommand {
    return RefundPaymentCommand(
        paymentId = paymentId,
        refundAmount = this.refundAmount,
        reason = this.reason,
    )
}

/**
 * RegisterPaymentMethodRequestV1 -> RegisterPaymentMethodCommand 변환
 */
fun RegisterPaymentMethodRequestV1.toCommand(userId: String): RegisterPaymentMethodCommand {
    return RegisterPaymentMethodCommand(
        userId = userId,
        type = this.type,
        alias = this.alias,
        cardNumber = this.cardNumber,
        cardExpiry = this.cardExpiry,
        cardCvv = this.cardCvv,
        isDefault = this.isDefault,
    )
}

/**
 * UpdatePaymentMethodRequestV1 -> UpdatePaymentMethodCommand 변환
 */
fun UpdatePaymentMethodRequestV1.toCommand(paymentMethodId: String): UpdatePaymentMethodCommand {
    return UpdatePaymentMethodCommand(
        paymentMethodId = paymentMethodId,
        cardExpiry = this.cardExpiry,
        alias = this.alias,
        isDefault = this.isDefault,
        isActive = this.isActive,
    )
} 