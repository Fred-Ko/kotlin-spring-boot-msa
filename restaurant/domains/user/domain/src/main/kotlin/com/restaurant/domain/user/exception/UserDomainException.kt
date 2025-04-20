package com.restaurant.domain.user.exception

import com.restaurant.common.core.error.ErrorCode
import com.restaurant.common.core.exception.DomainException
import com.restaurant.domain.user.error.UserDomainErrorCodes

// sealed class UserDomainException(...): DomainException(...) 제거

// 최상위 sealed class 는 marker 역할만 하거나, 공통 필드(message)만 가질 수 있음
sealed class UserDomainException(
    message: String,
) : DomainException(message) {
    // Rule 68, 73: 상위 클래스 DomainException의 abstract val errorCode를 override로 명시
    abstract override val errorCode: ErrorCode

    sealed class Validation(
        message: String,
        override val errorCode: ErrorCode = UserDomainErrorCodes.INVALID_INPUT,
    ) : UserDomainException(message) {
        data class InvalidEmailFormat(
            val email: String,
        ) : Validation("이메일 형식이 올바르지 않습니다: $email")

        data class InvalidPasswordFormat(
            val reason: String,
            override val errorCode: ErrorCode = UserDomainErrorCodes.INVALID_PASSWORD_FORMAT,
        ) : Validation("비밀번호 형식이 올바르지 않습니다: $reason")

        data class InvalidNameFormat(
            val name: String,
        ) : Validation("이름 형식이 올바르지 않습니다: $name")

        data class InvalidAddressFormat(
            val reason: String,
        ) : Validation("주소 형식이 올바르지 않습니다: $reason")
    }

    // 각 하위 sealed class 또는 data class 에서 errorCode 를 override
    sealed class User(
        message: String,
    ) : UserDomainException(message) {
        data class NotFound(
            val userId: String,
            // Rule 68: errorCode 를 override 로 명시적 지정
            override val errorCode: ErrorCode = UserDomainErrorCodes.USER_NOT_FOUND,
        ) : User("사용자를 찾을 수 없습니다: $userId") // 생성자에서 message 전달

        data class DuplicateEmail(
            val email: String,
            override val errorCode: ErrorCode = UserDomainErrorCodes.DUPLICATE_EMAIL,
        ) : User("이미 등록된 이메일입니다: $email")

        data class InvalidCredentials(
            override val errorCode: ErrorCode = UserDomainErrorCodes.PASSWORD_MISMATCH,
        ) : User(errorCode.message) // ErrorCode의 기본 메시지 사용

        data class InvalidInput(
            val reason: String,
            override val errorCode: ErrorCode = UserDomainErrorCodes.INVALID_INPUT,
        ) : User("잘못된 사용자 입력: $reason")
    }

    sealed class Address(
        message: String,
    ) : UserDomainException(message) {
        data class NotFound(
            val userId: String, // 사용자 ID도 String으로 변경
            val addressId: String, // Long -> String (AddressId.value.toString())
            override val errorCode: ErrorCode = UserDomainErrorCodes.ADDRESS_NOT_FOUND,
        ) : Address("사용자($userId)의 주소($addressId)를 찾을 수 없습니다.")

        data class MaxLimitExceeded(
            val userId: String,
            override val errorCode: ErrorCode = UserDomainErrorCodes.MAX_ADDRESS_LIMIT_EXCEEDED,
        ) : Address("사용자($userId)의 최대 주소 등록 개수를 초과했습니다.")

        data class DefaultAddressRemovalAttempt(
            val addressId: String, // Long -> String
            override val errorCode: ErrorCode = UserDomainErrorCodes.DEFAULT_ADDRESS_CANNOT_BE_REMOVED,
        ) : Address("기본 주소($addressId)는 삭제할 수 없습니다.")

        data class CannotRemoveLastAddress(
            val addressId: String,
            override val errorCode: ErrorCode = UserDomainErrorCodes.CANNOT_REMOVE_LAST_ADDRESS,
        ) : Address("마지막 주소($addressId)는 삭제할 수 없습니다.")
    }
}
