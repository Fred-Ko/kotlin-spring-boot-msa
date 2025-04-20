package com.restaurant.application.user.exception

import com.restaurant.application.user.error.UserApplicationErrorCode
import com.restaurant.common.core.error.ErrorCode
import com.restaurant.common.core.exception.ApplicationException

sealed class UserApplicationException(
    override val errorCode: ErrorCode,
    final override val message: String = errorCode.message,
) : ApplicationException(message) {
    data class InvalidInput(
        val details: String? = null,
    ) : UserApplicationException(
            UserApplicationErrorCode.INVALID_INPUT,
            if (details != null) "유효하지 않은 입력: $details" else UserApplicationErrorCode.INVALID_INPUT.message,
        )

    data class AuthenticationFailed(
        val reason: String? = null,
    ) : UserApplicationException(
            UserApplicationErrorCode.AUTHENTICATION_FAILED,
            if (reason != null) "인증 실패: $reason" else UserApplicationErrorCode.AUTHENTICATION_FAILED.message,
        )

    data class ExternalServiceError(
        val serviceName: String,
        val causeError: String? = null,
    ) : UserApplicationException(
            UserApplicationErrorCode.EXTERNAL_SERVICE_ERROR,
            "외부 서비스($serviceName) 호출 오류${causeError?.let { ": $it" } ?: ""}",
        )

    data class SystemError(
        val causeException: Throwable? = null,
    ) : UserApplicationException(
            UserApplicationErrorCode.SYSTEM_ERROR,
            "시스템 오류 발생${causeException?.message?.let { ": $it" } ?: ""}",
        )
}
