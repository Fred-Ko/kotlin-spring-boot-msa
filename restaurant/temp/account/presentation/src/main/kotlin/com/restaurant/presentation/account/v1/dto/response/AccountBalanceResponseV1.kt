package com.restaurant.presentation.account.v1.dto.response

import java.math.BigDecimal

/**
 * 계좌 잔액 응답 DTO
 */
data class AccountBalanceResponseV1(
    val accountId: Long,
    val balance: BigDecimal,
)
