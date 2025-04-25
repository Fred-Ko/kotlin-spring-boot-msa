package com.restaurant.application.account.query

/**
 * 계좌 잔액 조회 쿼리
 *
 * @property accountId 계좌 ID
 */
data class GetAccountBalanceQuery(
    val accountId: Long,
)
