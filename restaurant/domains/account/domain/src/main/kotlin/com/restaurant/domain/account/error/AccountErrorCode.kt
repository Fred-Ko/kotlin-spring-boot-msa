package com.restaurant.domain.account.error

/**
 * 계좌 도메인 에러 코드
 * 기술적 구현(HTTP 상태 코드)과 분리된 순수한 도메인 에러 코드입니다.
 */
enum class AccountErrorCode(
    val code: String,
    val message: String,
    val statusCode: Int,
) {
    ACCOUNT_NOT_FOUND(
        "ACCOUNT_001",
        "계좌를 찾을 수 없습니다.",
        404,
    ),
    INSUFFICIENT_BALANCE(
        "ACCOUNT_002",
        "잔액이 부족합니다.",
        400,
    ),
    TRANSACTION_NOT_FOUND(
        "ACCOUNT_003",
        "트랜잭션을 찾을 수 없습니다.",
        404,
    ),
    TRANSACTION_ALREADY_CANCELLED(
        "ACCOUNT_004",
        "이미 취소된 트랜잭션입니다.",
        400,
    ),
    INVALID_ACCOUNT_STATE(
        "ACCOUNT_005",
        "유효하지 않은 계좌 상태입니다.",
        400,
    ),
}
