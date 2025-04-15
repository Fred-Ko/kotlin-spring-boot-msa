package com.restaurant.domain.account.error

import com.restaurant.common.core.error.ErrorCode // 공통 ErrorCode 인터페이스 참조

/**
 * 계좌 도메인 에러 코드 인터페이스
 */
interface AccountDomainErrorCode : ErrorCode

/**
 * 계좌 도메인 에러 코드 Enum
 */
enum class DefaultAccountDomainErrorCode(
    override val code: String,
) : AccountDomainErrorCode {
    ACCOUNT_NOT_FOUND("ACCOUNT_001"), // 계좌 찾을 수 없음
    INSUFFICIENT_BALANCE("ACCOUNT_002"), // 잔액 부족
    TRANSACTION_NOT_FOUND("ACCOUNT_003"), // 트랜잭션 찾을 수 없음
    TRANSACTION_ALREADY_CANCELLED("ACCOUNT_004"), // 이미 취소된 트랜잭션
    INVALID_ACCOUNT_STATE("ACCOUNT_005"), // 유효하지 않은 계좌 상태 (예: ID 없음)
    // 필요한 다른 도메인 에러 코드 추가
}
