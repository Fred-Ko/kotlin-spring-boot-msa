package com.restaurant.payment.presentation.v1.command.extensions.dto.request

import com.restaurant.payment.application.command.dto.ProcessPaymentCommand
import com.restaurant.payment.application.command.dto.RefundPaymentCommand
import com.restaurant.payment.application.command.dto.RegisterBankTransferCommand
import com.restaurant.payment.application.command.dto.RegisterCreditCardCommand
import com.restaurant.payment.application.command.dto.RegisterPaymentMethodCommand
import com.restaurant.payment.domain.vo.AccountNumber
import com.restaurant.payment.domain.vo.Amount
import com.restaurant.payment.domain.vo.BankName
import com.restaurant.payment.domain.vo.CardCvv
import com.restaurant.payment.domain.vo.CardExpiry
import com.restaurant.payment.domain.vo.CardNumber
import com.restaurant.payment.domain.vo.OrderId
import com.restaurant.payment.domain.vo.PaymentId
import com.restaurant.payment.domain.vo.PaymentMethodId
import com.restaurant.payment.domain.vo.UserId
import com.restaurant.payment.presentation.v1.command.dto.request.ProcessPaymentRequestV1
import com.restaurant.payment.presentation.v1.command.dto.request.RefundPaymentRequestV1
import com.restaurant.payment.presentation.v1.command.dto.request.RegisterBankTransferRequest
import com.restaurant.payment.presentation.v1.command.dto.request.RegisterCreditCardRequest
import com.restaurant.payment.presentation.v1.command.dto.request.RegisterPaymentMethodRequest

fun ProcessPaymentRequestV1.toCommand(
    userId: UserId,
    orderId: OrderId,
): ProcessPaymentCommand =
    ProcessPaymentCommand(
        orderId = orderId,
        userId = userId,
        amount = Amount.of(this.amount),
        paymentMethodId = PaymentMethodId.of(this.paymentMethodId),
    )

fun RefundPaymentRequestV1.toCommand(paymentId: PaymentId): RefundPaymentCommand =
    RefundPaymentCommand(
        paymentId = paymentId,
        amount = Amount.of(this.amount),
        reason = this.reason,
    )

fun RegisterPaymentMethodRequest.toCommand(userId: UserId): RegisterPaymentMethodCommand =
    when (this) {
        is RegisterCreditCardRequest ->
            RegisterCreditCardCommand(
                userId = userId,
                alias = this.alias,
                isDefault = this.isDefault,
                cardNumber = CardNumber.of(this.cardNumber),
                cardExpiry = CardExpiry.of(this.cardExpiry),
                cardCvv = CardCvv.of(this.cardCvv),
            )
        is RegisterBankTransferRequest ->
            RegisterBankTransferCommand(
                userId = userId,
                alias = this.alias,
                isDefault = this.isDefault,
                bankName = BankName.of(this.bankName),
                accountNumber = AccountNumber.of(this.accountNumber),
            )
    }
