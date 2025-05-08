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

    override fun toString(): String = "********"

    companion object {
        /**
         * Creates a Password VO from a pre-encoded password string.
         * Basic validation is done in the init block.
         */
        fun of(encodedPassword: String): Password = Password(encodedPassword)
    }
}
