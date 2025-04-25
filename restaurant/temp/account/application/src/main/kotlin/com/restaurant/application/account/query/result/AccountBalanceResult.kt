package com.restaurant.application.account.query.result

/**
 * 계좌 잔액 쿼리 결과
 */
data class AccountBalanceResult(
    val accountId: Long,
    val balance: Long,
)
