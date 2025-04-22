package com.restaurant.application.user.error

import com.restaurant.common.core.error.ErrorCode
import com.restaurant.common.core.exception.ApplicationException

/**
 * User 애플리케이션 레이어 관련 예외
 */
sealed class UserApplicationException(
    override val errorCode: ErrorCode,
    override val message: String,
    override val cause: Throwable? = null,
) : ApplicationException(errorCode, message, cause) {
    data class UnexpectedError(
        override val cause: Throwable?,
    ) : UserApplicationException(
            UserApplicationErrorCode.UNEXPECTED_ERROR,
            "An unexpected error occurred in the user application layer.",
            cause,
        )

    data class UnexpectedApplicationError(
        // errorCode가 해당 도메인의 ApplicationErrorCode Enum 참조 (Rule 68)
        override val errorCode: UserApplicationErrorCode = UserApplicationErrorCode.SYSTEM_ERROR,
        override val message: String = "An unexpected application error occurred",
        val originalCause: Throwable? = null,
    ) : UserApplicationException(errorCode, message, originalCause)

    data class ExternalServiceFailure(
        override val errorCode: UserApplicationErrorCode = UserApplicationErrorCode.EXTERNAL_SERVICE_ERROR,
        override val message: String = "External service call failed",
        val externalService: String? = null,
        val originalCause: Throwable? = null,
    ) : UserApplicationException(errorCode, message, originalCause)

    data class AuthenticationFailed(
        override val errorCode: UserApplicationErrorCode = UserApplicationErrorCode.AUTHENTICATION_FAILED,
        override val message: String = "Authentication failed",
    ) : UserApplicationException(errorCode, message)

    // Add other specific application exceptions here
}
