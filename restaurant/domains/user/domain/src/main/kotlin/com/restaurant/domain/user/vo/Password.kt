package com.restaurant.domain.user.vo

import com.restaurant.domain.user.exception.UserDomainException

data class Password private constructor(
    val encodedValue: String,
) {
    init {
        if (encodedValue.isBlank()) {
            throw UserDomainException.Validation.InvalidPasswordFormat("비밀번호 값(인코딩 또는 raw)은 비어있을 수 없습니다.")
        }
    }

    companion object {
        private const val MIN_RAW_LENGTH = 8

        fun validateRaw(rawPassword: String) {
            if (rawPassword.isBlank()) {
                throw UserDomainException.Validation.InvalidPasswordFormat("비밀번호는 비어있을 수 없습니다.")
            }
            if (rawPassword.length < MIN_RAW_LENGTH) {
                throw UserDomainException.Validation.InvalidPasswordFormat("비밀번호는 최소 ${MIN_RAW_LENGTH}글자 이상이어야 합니다.")
            }
        }

        fun fromEncoded(encodedPassword: String): Password {
            if (encodedPassword.isBlank()) {
                throw UserDomainException.Validation.InvalidPasswordFormat("인코딩된 비밀번호 값은 비어있을 수 없습니다.")
            }
            return Password(encodedPassword)
        }
    }

    override fun toString(): String = "********"
}
