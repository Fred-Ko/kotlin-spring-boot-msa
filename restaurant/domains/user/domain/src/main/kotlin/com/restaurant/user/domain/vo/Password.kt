package com.restaurant.user.domain.vo

import com.restaurant.user.domain.exception.UserDomainException

/**
 * 비밀번호 Value Object
 */
@JvmInline
value class Password private constructor(
    val value: String,
) {
    init {
        if (value.isBlank()) {
            throw UserDomainException.Validation.InvalidPasswordFormat("Encoded password cannot be blank.")
        }
    }

    fun validate() {
        if (value.length < 8) {
            throw UserDomainException.Validation.InvalidPasswordFormat("Password must be at least 8 characters long.")
        }
        if (!value.contains(Regex("[A-Z]"))) {
            throw UserDomainException.Validation.InvalidPasswordFormat("Password must contain at least one uppercase letter.")
        }
        if (!value.contains(Regex("[a-z]"))) {
            throw UserDomainException.Validation.InvalidPasswordFormat("Password must contain at least one lowercase letter.")
        }
        if (!value.contains(Regex("[0-9]"))) {
            throw UserDomainException.Validation.InvalidPasswordFormat("Password must contain at least one digit.")
        }
        if (!value.contains(Regex("[!@#$%^&*()_+\\-=\\[\\]{};':\"\\\\|,.<>/?~`]"))) {
            throw UserDomainException.Validation.InvalidPasswordFormat("Password must contain at least one special character.")
        }
    }

    override fun toString(): String = "********"

    companion object {
        /**
         * Creates a Password VO from a pre-encoded password string.
         * Basic validation is done in the init block.
         */
        fun of(encodedPassword: String): Password = Password(encodedPassword)
    }
}
