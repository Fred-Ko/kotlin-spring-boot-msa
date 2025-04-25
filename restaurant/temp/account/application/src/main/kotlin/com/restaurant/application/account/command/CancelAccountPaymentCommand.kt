package com.restaurant.application.account.command

/**
 * 계좌 결제 취소 명령
 *
 * @property accountId 계좌 ID
 * @property orderId 주문 ID
 */
data class CancelAccountPaymentCommand(
    val accountId: Long,
    val orderId: String,
)
