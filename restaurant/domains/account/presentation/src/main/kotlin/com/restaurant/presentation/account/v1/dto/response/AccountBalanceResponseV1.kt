package com.restaurant.presentation.account.v1.dto.response

/**
 * 계좌 잔액 조회 응답
 */
data class AccountBalanceResponseV1(
    val accountId: Long,
    val balance: Long,
)
