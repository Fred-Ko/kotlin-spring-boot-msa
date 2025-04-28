package com.restaurant.user.application.error

import com.restaurant.common.error.ErrorCode

/**
 * User Application 레이어 관련 에러 코드 Enum
 */
enum class UserApplicationErrorCode(
    override val code: String,
    override val message: String,
) : ErrorCode {
    BAD_REQUEST("USER-APPLICATION-001", "Bad request received by application."),
    INVALID_INPUT("USER-APPLICATION-002", "Invalid input provided to application."),
    AUTHENTICATION_FAILED("USER-APPLICATION-003", "Authentication failed."),
    EXTERNAL_SERVICE_ERROR("USER-APPLICATION-004", "External service communication error."),
    UNEXPECTED_ERROR("USER-APPLICATION-500", "An unexpected error occurred in the application."),
    USER_NOT_FOUND_BY_EMAIL("USER-APPLICATION-005", "User not found by email"),
    INVALID_CREDENTIALS("USER-APPLICATION-006", "Invalid username or password"),
    USER_INACTIVE("USER-APPLICATION-007", "User account is inactive"),
    SYSTEM_ERROR("USER-APPLICATION-999", "처리 중 오류가 발생했습니다."),
    // 필요한 다른 Application 에러 코드 추가
}
