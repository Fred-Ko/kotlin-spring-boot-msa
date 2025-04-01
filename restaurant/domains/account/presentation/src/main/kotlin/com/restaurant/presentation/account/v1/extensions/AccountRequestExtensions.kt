package com.restaurant.presentation.account.v1.extensions

import com.restaurant.application.account.command.CancelAccountPaymentCommand
import com.restaurant.application.account.command.ProcessAccountPaymentCommand
import com.restaurant.application.account.command.RegisterAccountCommand
import com.restaurant.presentation.account.v1.dto.request.AccountRegisterRequestV1
import com.restaurant.presentation.account.v1.dto.request.CancelPaymentRequestV1
import com.restaurant.presentation.account.v1.dto.request.ProcessPaymentRequestV1

/**
 * 계좌 등록 요청 -> 명령 변환
 */
fun AccountRegisterRequestV1.toCommand(userId: Long): RegisterAccountCommand =
    RegisterAccountCommand(
        userId = userId,
        initialBalance = this.initialBalance,
    )

/**
 * 결제 처리 요청 -> 명령 변환
 */
fun ProcessPaymentRequestV1.toCommand(accountId: Long): ProcessAccountPaymentCommand =
    ProcessAccountPaymentCommand(
        accountId = accountId,
        amount = this.amount,
        orderId = this.orderId,
    )

/**
 * 결제 취소 요청 -> 명령 변환
 */
fun CancelPaymentRequestV1.toCommand(accountId: Long): CancelAccountPaymentCommand =
    CancelAccountPaymentCommand(
        accountId = accountId,
        orderId = this.orderId,
    )
