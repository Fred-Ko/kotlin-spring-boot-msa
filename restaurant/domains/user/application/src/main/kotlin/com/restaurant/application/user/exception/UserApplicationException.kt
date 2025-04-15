package com.restaurant.application.user.exception

import com.restaurant.application.user.common.UserErrorCode
import com.restaurant.common.core.exception.ApplicationException

sealed class UserApplicationException(
    override val message: String,
    open val errorCode: UserErrorCode,
) : ApplicationException(message) {
    sealed class Registration(
        override val message: String,
        override val errorCode: UserErrorCode,
    ) : UserApplicationException(message, errorCode) {
        data class DuplicateEmail(
            override val message: String = "이미 등록된 이메일입니다.",
        ) : Registration(message, UserErrorCode.DUPLICATE_EMAIL)

        data class InvalidInput(
            override val message: String = "유효하지 않은 입력입니다.",
        ) : Registration(message, UserErrorCode.INVALID_INPUT)

        data class SystemError(
            override val message: String = "사용자 등록 중 시스템 오류가 발생했습니다.",
        ) : Registration(message, UserErrorCode.SYSTEM_ERROR)
    }

    sealed class Authentication(
        override val message: String,
        override val errorCode: UserErrorCode,
    ) : UserApplicationException(message, errorCode) {
        data class InvalidCredentials(
            override val message: String = "이메일 또는 비밀번호가 올바르지 않습니다.",
        ) : Authentication(message, UserErrorCode.INVALID_CREDENTIALS)

        data class InvalidInput(
            override val message: String = "유효하지 않은 입력입니다.",
        ) : Authentication(message, UserErrorCode.INVALID_INPUT)
    }

    sealed class Profile(
        override val message: String,
        override val errorCode: UserErrorCode,
    ) : UserApplicationException(message, errorCode) {
        data class UpdateFailed(
            override val message: String = "사용자 정보 수정에 실패했습니다.",
        ) : Profile(message, UserErrorCode.UPDATE_FAILED)

        data class InvalidInput(
            override val message: String = "유효하지 않은 입력입니다.",
        ) : Profile(message, UserErrorCode.INVALID_INPUT)

        data class SystemError(
            override val message: String = "사용자 정보 수정 중 시스템 오류가 발생했습니다.",
        ) : Profile(message, UserErrorCode.SYSTEM_ERROR)
    }

    sealed class Password(
        override val message: String,
        override val errorCode: UserErrorCode,
    ) : UserApplicationException(message, errorCode) {
        data class CurrentPasswordMismatch(
            override val message: String = "현재 비밀번호가 일치하지 않습니다.",
        ) : Password(message, UserErrorCode.CURRENT_PASSWORD_MISMATCH)

        data class InvalidPassword(
            override val message: String = "현재 비밀번호가 올바르지 않습니다.",
        ) : Password(message, UserErrorCode.INVALID_PASSWORD)

        data class InvalidInput(
            override val message: String = "유효하지 않은 입력입니다.",
        ) : Password(message, UserErrorCode.INVALID_INPUT)

        data class UpdateFailed(
            override val message: String = "비밀번호 변경에 실패했습니다.",
        ) : Password(message, UserErrorCode.UPDATE_FAILED)

        data class SystemError(
            override val message: String = "비밀번호 변경 중 시스템 오류가 발생했습니다.",
        ) : Password(message, UserErrorCode.SYSTEM_ERROR)
    }

    sealed class Deletion(
        override val message: String,
        override val errorCode: UserErrorCode,
    ) : UserApplicationException(message, errorCode) {
        data class DeletionFailed(
            override val message: String = "사용자 삭제에 실패했습니다.",
        ) : Deletion(message, UserErrorCode.DELETION_FAILED)

        data class InvalidInput(
            override val message: String = "유효하지 않은 입력입니다.",
        ) : Deletion(message, UserErrorCode.INVALID_INPUT)

        data class SystemError(
            override val message: String = "사용자 삭제 중 시스템 오류가 발생했습니다.",
        ) : Deletion(message, UserErrorCode.SYSTEM_ERROR)
    }

    sealed class Query(
        override val message: String,
        override val errorCode: UserErrorCode,
    ) : UserApplicationException(message, errorCode) {
        data class NotFound(
            override val message: String = "사용자를 찾을 수 없습니다.",
        ) : Query(message, UserErrorCode.NOT_FOUND)

        data class InvalidInput(
            override val message: String = "유효하지 않은 입력입니다.",
        ) : Query(message, UserErrorCode.INVALID_INPUT)

        data class SystemError(
            override val message: String = "사용자 조회 중 시스템 오류가 발생했습니다.",
        ) : Query(message, UserErrorCode.SYSTEM_ERROR)
    }

    sealed class Address(
        override val message: String,
        override val errorCode: UserErrorCode,
    ) : UserApplicationException(message, errorCode) {
        data class NotFound(
            override val message: String = "주소를 찾을 수 없습니다.",
        ) : Address(message, UserErrorCode.ADDRESS_NOT_FOUND)

        data class MaxLimitExceeded(
            override val message: String = "최대 주소 등록 개수를 초과했습니다.",
        ) : Address(message, UserErrorCode.MAX_ADDRESS_LIMIT)

        data class InvalidInput(
            override val message: String = "유효하지 않은 주소 정보입니다.",
        ) : Address(message, UserErrorCode.INVALID_INPUT)

        data class SystemError(
            override val message: String = "주소 관련 작업 중 시스템 오류가 발생했습니다.",
        ) : Address(message, UserErrorCode.SYSTEM_ERROR)
    }
}
