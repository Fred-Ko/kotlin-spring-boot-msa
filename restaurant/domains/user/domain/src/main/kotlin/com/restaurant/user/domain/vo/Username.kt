package com.restaurant.user.domain.vo

import com.restaurant.user.domain.exception.UserDomainException
import java.io.Serializable

@JvmInline
value class Username private constructor(
    val value: String,
) : Serializable {
    init {

        if (value.isBlank() || value.length < 3 || value.length > 20) {
            throw UserDomainException.Validation.InvalidUsernameFormat(value)
        }
    }

    companion object {
        fun of(value: String): Username {
            try {
                return Username(value)
            } catch (e: UserDomainException.Validation.InvalidUsernameFormat) {
                throw e
            } catch (e: IllegalArgumentException) {
                throw UserDomainException.Validation.InvalidUsernameFormat(value)
            }
        }
    }

    override fun toString(): String = value
}
