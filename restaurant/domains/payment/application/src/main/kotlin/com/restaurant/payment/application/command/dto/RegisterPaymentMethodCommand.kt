package com.restaurant.payment.application.command.dto

import com.restaurant.payment.domain.vo.AccountNumber
import com.restaurant.payment.domain.vo.BankName
import com.restaurant.payment.domain.vo.CardCvv
import com.restaurant.payment.domain.vo.CardExpiry
import com.restaurant.payment.domain.vo.CardNumber
import com.restaurant.payment.domain.vo.UserId

sealed interface RegisterPaymentMethodCommand {
    val userId: UserId
    val alias: String
    val isDefault: Boolean
}

data class RegisterCreditCardCommand(
    override val userId: UserId,
    override val alias: String,
    override val isDefault: Boolean,
    val cardNumber: CardNumber,
    val cardExpiry: CardExpiry,
    val cardCvv: CardCvv,
) : RegisterPaymentMethodCommand

data class RegisterBankTransferCommand(
    override val userId: UserId,
    override val alias: String,
    override val isDefault: Boolean,
    val bankName: BankName,
    val accountNumber: AccountNumber,
) : RegisterPaymentMethodCommand
