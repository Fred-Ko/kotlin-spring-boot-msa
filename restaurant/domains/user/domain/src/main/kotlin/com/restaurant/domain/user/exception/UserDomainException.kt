package com.restaurant.domain.user.exception

import com.restaurant.common.core.error.ErrorCode
import com.restaurant.common.core.exception.DomainException

sealed class UserDomainException(
    override val message: String,
    open val errorCode: ErrorCode,
) : DomainException(message) {
    sealed class User(
        override val message: String,
        override val errorCode: ErrorCode,
    ) : UserDomainException(message, errorCode) {
        data class NotFound(
            val userId: String,
            override val errorCode: ErrorCode,
            override val message: String = "사용자를 찾을 수 없습니다: $userId",
        ) : User(message, errorCode)

        data class DuplicateEmail(
            val email: String,
            override val errorCode: ErrorCode,
            override val message: String = "이미 등록된 이메일입니다: $email",
        ) : User(message, errorCode)

        data class InvalidCredentials(
            override val errorCode: ErrorCode,
            override val message: String = "이메일 또는 비밀번호가 올바르지 않습니다.",
        ) : User(message, errorCode)
    }

    sealed class Address(
        override val message: String,
        override val errorCode: ErrorCode,
    ) : UserDomainException(message, errorCode) {
        data class NotFound(
            val userId: Long,
            val addressId: Long,
            override val errorCode: ErrorCode,
            override val message: String = "사용자($userId)의 주소($addressId)를 찾을 수 없습니다.",
        ) : Address(message, errorCode)

        data class MaxLimitExceeded(
            val userId: Long,
            override val errorCode: ErrorCode,
            override val message: String = "사용자($userId)의 최대 주소 등록 개수를 초과했습니다.",
        ) : Address(message, errorCode)
    }
}
