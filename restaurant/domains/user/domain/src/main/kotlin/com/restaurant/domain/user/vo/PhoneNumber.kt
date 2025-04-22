package com.restaurant.domain.user.vo

import com.restaurant.domain.user.error.UserDomainException

data class PhoneNumber private constructor(
    val value: String,
) {
    init {
        if (!value.matches(PHONE_NUMBER_REGEX)) {
            throw UserDomainException.Validation.InvalidPhoneNumberFormat(value)
        }
    }

    companion object {
        private val PHONE_NUMBER_REGEX = Regex("^\\d{3}-\\d{3,4}-\\d{4}$")

        fun of(value: String): PhoneNumber = PhoneNumber(value)
    }

    override fun toString(): String = value
}
