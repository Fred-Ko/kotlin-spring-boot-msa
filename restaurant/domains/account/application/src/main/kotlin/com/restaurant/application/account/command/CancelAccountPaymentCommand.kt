package com.restaurant.application.account.command

/**
 * 계좌 결제 취소 커맨드
 */
data class CancelAccountPaymentCommand(
    val accountId: Long,
    val orderId: Long,
)
