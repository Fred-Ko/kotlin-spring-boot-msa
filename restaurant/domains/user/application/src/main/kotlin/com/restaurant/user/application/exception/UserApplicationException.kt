package com.restaurant.user.application.exception

import com.restaurant.common.domain.error.ErrorCode
import com.restaurant.common.domain.exception.ApplicationException
import com.restaurant.user.application.error.UserApplicationErrorCode

/**
 * User Application 레이어 관련 예외 정의 (Rule 68)
 */
sealed class UserApplicationException(
    final override val errorCode: ErrorCode,
    message: String? = null,
    cause: Throwable? = null,
) : ApplicationException(errorCode, message ?: errorCode.message, cause) {
    /**
     * 인증 실패 관련 예외
     */
    class AuthenticationFailed(
        message: String? = UserApplicationErrorCode.AUTHENTICATION_FAILED.message,
        cause: Throwable? = null,
    ) : UserApplicationException(UserApplicationErrorCode.AUTHENTICATION_FAILED, message, cause)

    /**
     * 잘못된 입력값 관련 예외 (Application 레벨) - e.g., Invalid UUID format
     */
    class BadRequest(
        message: String? = UserApplicationErrorCode.BAD_REQUEST.message,
        cause: Throwable? = null,
    ) : UserApplicationException(UserApplicationErrorCode.BAD_REQUEST, message, cause)

    /**
     * 잘못된 입력값 관련 예외 (Application 레벨) - more specific than BadRequest
     */
    class InvalidInput(
        message: String? = UserApplicationErrorCode.INVALID_INPUT.message,
        cause: Throwable? = null,
    ) : UserApplicationException(UserApplicationErrorCode.INVALID_INPUT, message, cause)

    /**
     * 외부 서비스 연동 오류
     */
    class ExternalServiceError(
        message: String? = UserApplicationErrorCode.EXTERNAL_SERVICE_ERROR.message,
        cause: Throwable? = null,
    ) : UserApplicationException(UserApplicationErrorCode.EXTERNAL_SERVICE_ERROR, message, cause)

    /**
     * 예상치 못한 시스템 오류 (Application 레벨)
     */
    class UnexpectedError(
        message: String? = UserApplicationErrorCode.UNEXPECTED_ERROR.message,
        cause: Throwable? = null,
    ) : UserApplicationException(UserApplicationErrorCode.UNEXPECTED_ERROR, message, cause)

    /**
     * Login specific exceptions
     */
    class UserNotFound(
        errorCode: ErrorCode,
        identifier: String,
    ) : UserApplicationException(errorCode, "User not found with identifier: $identifier")

    class InvalidCredentials(
        errorCode: ErrorCode,
    ) : UserApplicationException(errorCode)

    class UserInactive(
        errorCode: ErrorCode,
        userId: String,
    ) : UserApplicationException(errorCode, "User is inactive: $userId")
}
