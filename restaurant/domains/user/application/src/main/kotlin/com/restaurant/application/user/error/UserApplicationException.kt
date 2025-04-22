package com.restaurant.application.user.error

import com.restaurant.common.core.exception.ApplicationException

/**
 * User 애플리케이션 레이어 관련 예외
 */
sealed class UserApplicationException(
    override val errorCode: UserApplicationErrorCode,
    override val message: String = errorCode.message,
    override val cause: Throwable? = null,
) : ApplicationException(message, cause) {
    data class UnexpectedError(
        val errCode: UserApplicationErrorCode = UserApplicationErrorCode.UNEXPECTED_ERROR,
        val originalCause: Throwable? = null,
    ) : UserApplicationException(errCode, errCode.message, originalCause)

    data class ExternalServiceError(
        val originalCause: Throwable? = null,
        val errCode: UserApplicationErrorCode = UserApplicationErrorCode.EXTERNAL_SERVICE_ERROR,
    ) : UserApplicationException(errCode, errCode.message, originalCause)

    data class AuthenticationFailed(
        val errCode: UserApplicationErrorCode = UserApplicationErrorCode.AUTHENTICATION_FAILED,
    ) : UserApplicationException(errCode, errCode.message)

    data class SystemError(
        val originalCause: Throwable? = null,
    ) : UserApplicationException(
            UserApplicationErrorCode.SYSTEM_ERROR,
            UserApplicationErrorCode.SYSTEM_ERROR.message,
            originalCause,
        )
    // Add other specific application exceptions here
}
