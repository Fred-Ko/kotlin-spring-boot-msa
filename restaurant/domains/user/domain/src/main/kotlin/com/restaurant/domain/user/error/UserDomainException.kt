package com.restaurant.domain.user.error

import com.restaurant.common.core.error.ErrorCode
import com.restaurant.common.core.exception.DomainException

/**
 * User 도메인 관련 예외 최상위 클래스
 */
sealed class UserDomainException(
    override val errorCode: ErrorCode,
    override val message: String,
    override val cause: Throwable? = null,
) : DomainException(message) { // DomainException 생성자에는 message만 전달
    // Rule 68: Validation 관련 DomainException 하위 타입 정의
    // Rule 14, 61: VO 유효성 검사 실패 시 상속하는 베이스 예외 타입
    // sealed class Validation(...) : UserDomainException(...) { ... }

    data class UserNotFound(
        val userId: String,
    ) : UserDomainException(
            UserDomainErrorCodes.USER_NOT_FOUND,
            "User not found with ID: $userId",
        )

    data class DuplicateUsername(
        val username: String,
    ) : UserDomainException(
            UserDomainErrorCodes.DUPLICATE_USERNAME,
            "Username already exists: $username",
        )

    data class InvalidCredentials(
        override val errorCode: UserDomainErrorCodes = UserDomainErrorCodes.PASSWORD_MISMATCH, // INVALID_PASSWORD -> PASSWORD_MISMATCH
        override val message: String = "Invalid username or password",
    ) : UserDomainException(errorCode, message, null)

    // Add other specific domain exceptions here
}

// UserDomainErrorCodes Enum에 필요한 추가 코드 (예시)
