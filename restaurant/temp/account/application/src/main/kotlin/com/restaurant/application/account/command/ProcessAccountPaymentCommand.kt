package com.restaurant.application.account.command

import java.math.BigDecimal

/**
 * 계좌 결제 처리 명령
 *
 * @property accountId 계좌 ID
 * @property amount 결제 금액
 * @property orderId 주문 ID
 */
data class ProcessAccountPaymentCommand(
    val accountId: Long,
    val amount: BigDecimal,
    val orderId: String,
)
