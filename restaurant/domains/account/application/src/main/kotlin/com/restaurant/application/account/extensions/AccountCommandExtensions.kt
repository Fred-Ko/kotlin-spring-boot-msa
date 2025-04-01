package com.restaurant.application.account.extensions

import com.restaurant.application.account.command.CancelAccountPaymentCommand
import com.restaurant.application.account.command.DeleteAccountCommand
import com.restaurant.application.account.command.ProcessAccountPaymentCommand
import com.restaurant.application.account.command.RegisterAccountCommand
import com.restaurant.domain.account.vo.AccountId
import com.restaurant.domain.account.vo.Money
import com.restaurant.domain.account.vo.OrderId
import com.restaurant.domain.account.vo.UserId

// RegisterAccountCommand 확장 함수
fun RegisterAccountCommand.toUserId(): UserId = UserId.of(this.userId)

fun RegisterAccountCommand.toInitialBalance(): Money = Money.of(this.initialBalance)

// DeleteAccountCommand 확장 함수
fun DeleteAccountCommand.toAccountId(): AccountId = AccountId.of(this.accountId)

// ProcessAccountPaymentCommand 확장 함수
fun ProcessAccountPaymentCommand.toAccountId(): AccountId = AccountId.of(this.accountId)

fun ProcessAccountPaymentCommand.toAmount(): Money = Money.of(this.amount)

fun ProcessAccountPaymentCommand.toOrderId(): OrderId = OrderId.of(this.orderId)

// CancelAccountPaymentCommand 확장 함수
fun CancelAccountPaymentCommand.toAccountId(): AccountId = AccountId.of(this.accountId)

fun CancelAccountPaymentCommand.toOrderId(): OrderId = OrderId.of(this.orderId)
