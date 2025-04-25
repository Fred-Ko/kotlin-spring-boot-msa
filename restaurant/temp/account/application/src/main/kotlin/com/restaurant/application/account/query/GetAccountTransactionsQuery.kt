package com.restaurant.application.account.query

/**
 * 계좌 트랜잭션 조회 쿼리
 *
 * @property accountId 계좌 ID
 * @property cursor 커서 (다음 페이지 조회 시 사용)
 * @property limit 조회할 항목 수
 */
data class GetAccountTransactionsQuery(
    val accountId: Long,
    val cursor: String? = null,
    val limit: Int = 10,
)
