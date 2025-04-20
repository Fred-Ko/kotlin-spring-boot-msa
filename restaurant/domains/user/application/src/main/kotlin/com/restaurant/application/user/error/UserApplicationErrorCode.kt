package com.restaurant.application.user.error

import com.restaurant.common.core.error.ErrorCode
import org.springframework.http.HttpStatus

/**
 * User Application 레이어 관련 에러 코드 Enum
 */
enum class UserApplicationErrorCode(
    override val code: String,
    override val status: HttpStatus,
    override val message: String,
) : ErrorCode {
    INVALID_INPUT("USER-APPLICATION-001", HttpStatus.BAD_REQUEST, "입력값이 유효하지 않습니다."),
    AUTHENTICATION_FAILED("USER-APPLICATION-002", HttpStatus.UNAUTHORIZED, "인증에 실패했습니다."), // 예시: JWT 토큰 검증 실패 등
    EXTERNAL_SERVICE_ERROR("USER-APPLICATION-003", HttpStatus.INTERNAL_SERVER_ERROR, "외부 서비스 호출에 실패했습니다."),
    SYSTEM_ERROR("USER-APPLICATION-999", HttpStatus.INTERNAL_SERVER_ERROR, "처리 중 오류가 발생했습니다."),
    // 필요한 다른 Application 에러 코드 추가
}
