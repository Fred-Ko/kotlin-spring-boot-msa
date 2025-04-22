package com.restaurant.application.user.error

import com.restaurant.common.core.error.ErrorCode

/**
 * User Application 레이어 관련 에러 코드 Enum
 */
enum class UserApplicationErrorCode(
    override val code: String,
    override val message: String,
) : ErrorCode {
    // ErrorCode 인터페이스의 속성을 override
    INVALID_INPUT("USER-APPLICATION-001", "입력값이 유효하지 않습니다."),
    AUTHENTICATION_FAILED("USER-APPLICATION-002", "인증에 실패했습니다."), // 예시: JWT 토큰 검증 실패 등
    EXTERNAL_SERVICE_ERROR("USER-APPLICATION-003", "외부 서비스 호출에 실패했습니다."),
    UNEXPECTED_ERROR("USER-APPLICATION-998", "An unexpected error occurred."),
    SYSTEM_ERROR("USER-APPLICATION-999", "처리 중 오류가 발생했습니다."),
    // 필요한 다른 Application 에러 코드 추가
}
