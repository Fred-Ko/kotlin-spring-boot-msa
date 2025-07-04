package com.restaurant.payment.presentation.v1.command.extensions.dto.request

import com.restaurant.payment.application.command.dto.ProcessPaymentCommand
import com.restaurant.payment.application.command.dto.RefundPaymentCommand
import com.restaurant.payment.application.command.dto.RegisterPaymentMethodCommand
import com.restaurant.payment.application.command.dto.RegisterCreditCardCommand
import com.restaurant.payment.application.command.dto.RegisterBankTransferCommand
import com.restaurant.payment.domain.vo.AccountNumber
import com.restaurant.payment.domain.vo.BankName
import com.restaurant.payment.domain.vo.CardCvv
import com.restaurant.payment.domain.vo.CardExpiry
import com.restaurant.payment.domain.vo.CardNumber
import com.restaurant.payment.domain.vo.UserId
import com.restaurant.payment.presentation.v1.command.dto.request.ProcessPaymentRequestV1
import com.restaurant.payment.presentation.v1.command.dto.request.RefundPaymentRequestV1
import com.restaurant.payment.presentation.v1.command.dto.request.RegisterPaymentMethodRequest
import com.restaurant.payment.presentation.v1.command.dto.request.RegisterCreditCardRequest
import com.restaurant.payment.presentation.v1.command.dto.request.RegisterBankTransferRequest

/**
 * Payment Command Request DTO Extensions (Rule 5, 7, 58, 59)
 * Presentation Request -> Application Command DTO conversion
 */

/**
 * ProcessPaymentRequestV1을 ProcessPaymentCommand로 변환합니다.
 */
fun ProcessPaymentRequestV1.toCommand(
    orderId: String,
    userId: String,
): ProcessPaymentCommand =
    ProcessPaymentCommand(
        orderId = orderId,
        userId = userId,
        amount = this.amount,
        paymentMethodId = this.paymentMethodId,
    )

/**
 * RefundPaymentRequestV1을 RefundPaymentCommand로 변환합니다.
 */
fun RefundPaymentRequestV1.toCommand(paymentId: String): RefundPaymentCommand =
    RefundPaymentCommand(
        paymentId = paymentId,
        refundAmount = this.refundAmount,
        reason = this.reason,
    )

/**
 * RegisterPaymentMethodRequest를 RegisterPaymentMethodCommand로 변환합니다.
 */
fun RegisterPaymentMethodRequest.toCommand(userId: String): RegisterPaymentMethodCommand =
    when (this) {
        is RegisterCreditCardRequest -> RegisterCreditCardCommand(
            userId = UserId.ofString(userId),
            alias = this.alias,
            cardNumber = CardNumber.of(this.cardNumber),
            cardExpiry = CardExpiry.of(this.cardExpiry),
            cardCvv = CardCvv.of(this.cardCvv),
            isDefault = this.isDefault,
        )
        is RegisterBankTransferRequest -> RegisterBankTransferCommand(
            userId = UserId.ofString(userId),
            alias = this.alias,
            bankName = BankName.of(this.bankName),
            accountNumber = AccountNumber.of(this.accountNumber),
            isDefault = this.isDefault,
        )
    }