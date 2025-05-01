package com.restaurant.user.domain.error

import com.restaurant.common.domain.error.ErrorCode // Add this

/**
 * User 도메인 비즈니스 규칙 위반 관련 에러 코드 Enum (Rule 67)
 */
enum class UserDomainErrorCodes(
    override val code: String,
    override val message: String,
) : ErrorCode {
    USER_NOT_FOUND("USER-DOMAIN-001", "사용자를 찾을 수 없습니다."),
    DUPLICATE_EMAIL("USER-DOMAIN-002", "이미 사용중인 이메일입니다."),
    DUPLICATE_USERNAME("USER-DOMAIN-003", "이미 사용중인 사용자 이름입니다."),
    PASSWORD_MISMATCH("USER-DOMAIN-004", "비밀번호가 일치하지 않습니다."),
    USER_ALREADY_WITHDRAWN("USER-DOMAIN-005", "이미 탈퇴한 사용자입니다."),
    ADDRESS_NOT_FOUND("USER-DOMAIN-006", "주소를 찾을 수 없습니다."),
    DUPLICATE_ADDRESS_ID("USER-DOMAIN-007", "이미 존재하는 주소 ID 입니다."),
    MAX_ADDRESS_LIMIT_EXCEEDED("USER-DOMAIN-008", "최대 주소 등록 개수를 초과했습니다."),
    DEFAULT_ADDRESS_CANNOT_BE_REMOVED("USER-DOMAIN-009", "기본 주소는 삭제할 수 없습니다."),
    CANNOT_REMOVE_LAST_ADDRESS("USER-DOMAIN-010", "마지막 주소는 삭제할 수 없습니다."),
    INVALID_EMAIL_FORMAT("USER-DOMAIN-101", "잘못된 이메일 형식입니다."),
    INVALID_USERNAME_FORMAT("USER-DOMAIN-102", "사용자 이름 형식이 올바르지 않습니다."),
    INVALID_PASSWORD_FORMAT("USER-DOMAIN-103", "비밀번호 형식이 올바르지 않습니다."),
    INVALID_NAME_FORMAT("USER-DOMAIN-104", "잘못된 이름 형식입니다."),
    INVALID_ADDRESS_FORMAT("USER-DOMAIN-105", "잘못된 주소 형식입니다."),
    INVALID_PHONE_NUMBER_FORMAT("USER-DOMAIN-106", "잘못된 전화번호 형식입니다."),
    INVALID_USER_ID_FORMAT("USER-DOMAIN-107", "잘못된 사용자 ID 형식입니다."),
    INVALID_ADDRESS_ID_FORMAT("USER-DOMAIN-108", "잘못된 주소 ID 형식입니다."),
    ADDRESS_ID_MISMATCH("USER-DOMAIN-201", "주소 ID가 일치하지 않습니다."),
    INVALID_CREDENTIALS("USER-DOMAIN-056", "Invalid credentials"),
    ADMIN_CANNOT_BE_WITHDRAWN("USER-DOMAIN-057", "Admin user cannot be withdrawn"),
    MULTIPLE_DEFAULT_ADDRESSES("USER-DOMAIN-107", "Cannot have multiple default addresses"),
    DEFAULT_ADDRESS_NOT_FOUND("USER-DOMAIN-108", "Default address not found"),
    PERSISTENCE_ERROR("USER-DOMAIN-901", "Persistence error occurred"),
}
