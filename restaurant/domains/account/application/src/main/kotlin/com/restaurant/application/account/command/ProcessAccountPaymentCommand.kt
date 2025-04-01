package com.restaurant.application.account.command

/**
 * 계좌 결제 처리 커맨드
 */
data class ProcessAccountPaymentCommand(
    val accountId: Long,
    val amount: Long,
    val orderId: Long,
)
