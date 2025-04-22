package com.restaurant.domain.user.error

import com.restaurant.common.core.error.ErrorCode

/**
 * User 도메인 비즈니스 규칙 위반 관련 에러 코드 Enum
 */
enum class UserDomainErrorCodes(
    override val code: String,
    override val message: String,
) : ErrorCode {
    USER_NOT_FOUND("USER-DOMAIN-001", "사용자를 찾을 수 없습니다."),
    DUPLICATE_USERNAME("USER-DOMAIN-002", "이미 사용중인 사용자 이름입니다."), // DUPLICATE_EMAIL -> DUPLICATE_USERNAME
    PASSWORD_MISMATCH("USER-DOMAIN-003", "비밀번호가 일치하지 않습니다."),
    ADDRESS_NOT_FOUND("USER-DOMAIN-004", "주소를 찾을 수 없습니다."),
    MAX_ADDRESS_LIMIT_EXCEEDED("USER-DOMAIN-005", "최대 주소 등록 개수를 초과했습니다."),
    DEFAULT_ADDRESS_CANNOT_BE_REMOVED("USER-DOMAIN-006", "기본 주소는 삭제할 수 없습니다."),
    CANNOT_REMOVE_LAST_ADDRESS("USER-DOMAIN-007", "마지막 주소는 삭제할 수 없습니다."),
    INVALID_PASSWORD_FORMAT("USER-DOMAIN-008", "비밀번호 형식이 올바르지 않습니다."),
    INVALID_INPUT("USER-DOMAIN-009", "입력값이 유효하지 않습니다."),
}
