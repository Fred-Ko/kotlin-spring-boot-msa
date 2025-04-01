package com.restaurant.presentation.account.v1.common

import com.restaurant.common.core.error.ErrorCode
import org.springframework.http.HttpStatus

/**
 * 계좌 관련 에러 코드
 * 기술 가이드 문서의 12.6 에러 코드 및 Type URI 규칙에 따라 정의
 */
enum class AccountErrorCode(
    override val code: String,
    override val message: String,
    override val status: HttpStatus,
) : ErrorCode {
    ACCOUNT_NOT_FOUND(
        "ACCOUNT_NOT_FOUND",
        "계좌를 찾을 수 없습니다.",
        HttpStatus.NOT_FOUND,
    ),
    INSUFFICIENT_BALANCE(
        "INSUFFICIENT_BALANCE",
        "잔액이 부족합니다.",
        HttpStatus.BAD_REQUEST,
    ),
    TRANSACTION_NOT_FOUND(
        "TRANSACTION_NOT_FOUND",
        "결제 내역을 찾을 수 없습니다.",
        HttpStatus.NOT_FOUND,
    ),
    PAYMENT_PROCESSING_FAILED(
        "PAYMENT_PROCESSING_FAILED",
        "결제 처리 중 오류가 발생했습니다.",
        HttpStatus.BAD_REQUEST,
    ),
    PAYMENT_CANCELLATION_FAILED(
        "PAYMENT_CANCELLATION_FAILED",
        "결제 취소 중 오류가 발생했습니다.",
        HttpStatus.BAD_REQUEST,
    ),
    UNKNOWN(
        "UNKNOWN_ERROR",
        "알 수 없는 오류가 발생했습니다.",
        HttpStatus.INTERNAL_SERVER_ERROR,
    ),
    ;

    companion object {
        /**
         * 에러 코드로 AccountErrorCode 객체를 찾음
         * 일치하는 코드가 없을 경우 UNKNOWN 반환
         */
        fun fromCode(code: String?): AccountErrorCode = entries.find { it.code == code } ?: UNKNOWN
    }
}
