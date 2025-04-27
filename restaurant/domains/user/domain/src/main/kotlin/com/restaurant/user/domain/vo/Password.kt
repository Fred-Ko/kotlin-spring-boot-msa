package com.restaurant.user.domain.vo

import com.restaurant.user.domain.exception.UserDomainException
import io.github.oshai.kotlinlogging.KotlinLogging

private val log = KotlinLogging.logger {}

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
        // Example: Add more checks for encoded format if applicable
        // if (!value.startsWith("{bcrypt}")) { // Example check
        //     throw UserDomainException.Validation.InvalidPasswordFormat("Encoded password format is invalid.")
        // }
    }

    override fun toString(): String = "********"

    companion object {
        /**
         * Creates a Password VO from a pre-encoded password string.
         * Basic validation is done in the init block.
         */
        fun of(encodedPassword: String): Password {
            // Validation moved to init block
            return Password(encodedPassword)
        }

        // encode method removed - responsibility of Application layer
        // fun encode(rawPassword: String, passwordEncoder: PasswordEncoder): Password { ... }

        // matches method removed - responsibility of Application layer
        // fun matches(rawPassword: String, passwordEncoder: PasswordEncoder): Boolean { ... }
    }
}
