package com.restaurant.application.account.error

import com.restaurant.common.core.error.ErrorCode
import org.springframework.http.HttpStatus

enum class AccountApplicationErrorCode(
    override val code: String,
    override val message: String,
    override val status: HttpStatus,
) : ErrorCode {
    ACCOUNT_NOT_FOUND(
        "ACCOUNT-APPLICATION-001",
        "계좌를 찾을 수 없습니다.",
        HttpStatus.NOT_FOUND,
    ),
    INSUFFICIENT_BALANCE(
        "ACCOUNT-APPLICATION-002",
        "잔액이 부족합니다.",
        HttpStatus.BAD_REQUEST,
    ),
    TRANSACTION_NOT_FOUND(
        "ACCOUNT-APPLICATION-003",
        "트랜잭션을 찾을 수 없습니다.",
        HttpStatus.NOT_FOUND,
    ),
    TRANSACTION_ALREADY_CANCELLED(
        "ACCOUNT-APPLICATION-004",
        "이미 취소된 트랜잭션입니다.",
        HttpStatus.BAD_REQUEST,
    ),
    SYSTEM_ERROR(
        "ACCOUNT-APPLICATION-999",
        "시스템 오류가 발생했습니다.",
        HttpStatus.INTERNAL_SERVER_ERROR,
    ),
}
