package com.restaurant.user.domain.vo

import com.restaurant.user.domain.exception.UserDomainException
import java.io.Serializable

@JvmInline
value class Username private constructor(
    val value: String,
) : Serializable {
    init {
        // Add validation logic here if needed (e.g., length, characters)
        if (value.isBlank() || value.length < 3 || value.length > 20) {
            throw UserDomainException.Validation.InvalidUsernameFormat("Username must be between 3 and 20 characters: '$value'")
        }
        // Example: Allow only alphanumeric characters
        // if (!value.matches(Regex("^[a-zA-Z0-9]*$"))) {
        //    throw UserDomainException.Validation.InvalidUsernameFormat("Username must be alphanumeric: '$value'")
        // }
    }

    companion object {
        fun of(value: String): Username {
            // Perform validation before creating
            try {
                // Reuse init block validation implicitly by calling private constructor
                return Username(value)
            } catch (e: UserDomainException.Validation.InvalidUsernameFormat) {
                // Re-throw specific exception if caught from init
                throw e
            } catch (e: IllegalArgumentException) {
                // Catch potential issues from require/check if used in init
                throw UserDomainException.Validation.InvalidUsernameFormat("Invalid username format: ${e.message}")
            }
        }
    }

    override fun toString(): String = value
}
