package com.restaurant.application.account.command

import java.math.BigDecimal

/**
 * 계좌 등록 명령
 *
 * @property userId 사용자 ID
 * @property initialBalance 초기 잔액
 */
data class RegisterAccountCommand(
    val userId: Long,
    val initialBalance: BigDecimal,
)
