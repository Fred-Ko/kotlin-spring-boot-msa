package com.restaurant.application.account.command

/**
 * 계좌 등록 커맨드
 */
data class RegisterAccountCommand(
    val userId: Long,
    val initialBalance: Long = 0,
)
