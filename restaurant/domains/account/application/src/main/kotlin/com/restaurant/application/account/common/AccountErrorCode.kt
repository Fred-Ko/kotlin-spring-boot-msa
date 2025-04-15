package com.restaurant.application.account.common

import com.restaurant.common.core.error.ErrorCode
import org.springframework.http.HttpStatus

/**
 * 계좌 관련 에러 코드
 */
enum class AccountErrorCode(
    override val code: String,
    override val message: String,
    override val status: HttpStatus,
) : ErrorCode {
    // 계좌 관련 에러
    NOT_FOUND("ACCOUNT_NOT_FOUND", "계좌를 찾을 수 없습니다.", HttpStatus.NOT_FOUND),
    INSUFFICIENT_BALANCE("ACCOUNT_INSUFFICIENT_BALANCE", "잔액이 부족합니다.", HttpStatus.BAD_REQUEST),

    // 트랜잭션 관련 에러
    TRANSACTION_NOT_FOUND("TRANSACTION_NOT_FOUND", "트랜잭션을 찾을 수 없습니다.", HttpStatus.NOT_FOUND),
    TRANSACTION_ALREADY_CANCELLED("TRANSACTION_ALREADY_CANCELLED", "이미 취소된 트랜잭션입니다.", HttpStatus.BAD_REQUEST),

    // 시스템 에러
    SYSTEM_ERROR("ACCOUNT_SYSTEM_ERROR", "시스템 오류가 발생했습니다.", HttpStatus.INTERNAL_SERVER_ERROR),
    ;

    companion object {
        fun fromCode(code: String?): AccountErrorCode = entries.find { it.code == code } ?: SYSTEM_ERROR
    }
}
