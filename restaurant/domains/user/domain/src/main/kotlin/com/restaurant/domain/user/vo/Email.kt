package com.restaurant.domain.user.vo

import com.restaurant.domain.user.exception.UserDomainException

data class Email private constructor(
    val value: String,
) {
    init {
        if (!value.matches(EMAIL_REGEX)) {
            throw UserDomainException.Validation.InvalidEmailFormat(value)
        }
    }

    companion object {
        private val EMAIL_REGEX = Regex("^[A-Za-z0-9+_.-]+@[A-Za-z0-9.-]+\\.[A-Za-z0-9.-]+$")

        fun of(value: String): Email = Email(value)
    }

    override fun toString(): String = value
}
