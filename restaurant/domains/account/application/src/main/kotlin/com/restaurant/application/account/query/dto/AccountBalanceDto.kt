package com.restaurant.application.account.query.dto

import java.math.BigDecimal

/**
 * 계좌 잔액 정보 DTO
 *
 * @property accountId 계좌 ID
 * @property balance 현재 잔액
 */
data class AccountBalanceDto(
    val accountId: Long,
    val balance: BigDecimal,
)
