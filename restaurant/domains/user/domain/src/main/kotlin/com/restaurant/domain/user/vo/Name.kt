package com.restaurant.domain.user.vo

import com.restaurant.domain.user.error.UserDomainException

data class Name private constructor(
    val value: String,
) {
    init {
        if (value.isBlank()) {
            throw UserDomainException.Validation.InvalidNameFormat(value)
        }
    }

    companion object {
        fun of(name: String): Name = Name(name)
    }

    override fun toString(): String = value
}
