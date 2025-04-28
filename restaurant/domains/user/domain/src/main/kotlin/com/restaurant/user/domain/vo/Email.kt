package com.restaurant.user.domain.vo

import com.restaurant.user.domain.exception.UserDomainException
import java.util.regex.Pattern

/**
 * Value object representing an email address.
 * This class ensures that the email address is valid according to a basic pattern.
 */
@JvmInline
value class Email private constructor(
    val value: String,
) {
    companion object {
        private val EMAIL_PATTERN =
            Regex(
                "[a-zA-Z0-9+._%\\-]{1,256}" +
                    "@" +
                    "[a-zA-Z0-9][a-zA-Z0-9\\-]{0,64}" +
                    "(" +
                    "\\." +
                    "[a-zA-Z0-9][a-zA-Z0-9\\-]{0,25}" +
                    ")+",
            )

        /**
         * Creates a new Email from a string value.
         * @throws UserDomainException.Validation if the email format is invalid
         */
        fun of(value: String): Email {
            if (!EMAIL_PATTERN.matches(value)) {
                throw UserDomainException.Validation.InvalidEmailFormat(value)
            }
            return Email(value.lowercase())
        }
    }

    override fun toString(): String = value
}
