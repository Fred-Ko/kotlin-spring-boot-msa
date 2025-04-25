package com.restaurant.presentation.account.v1.query.dto.response

import java.math.BigDecimal

/**
 * 계좌 잔액 정보 응답 DTO
 *
 * @property accountId 계좌 ID
 * @property balance 현재 잔액
 */
data class AccountBalanceResponseV1(
    val accountId: Long,
    val balance: BigDecimal,
)
