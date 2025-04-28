package com.restaurant.user.domain.vo
import com.restaurant.user.domain.exception.UserDomainException
import java.io.Serializable
import java.util.UUID

/**
 * Value object representing a unique user identifier.
 * This class wraps a UUID and provides type safety and validation.
 */
@JvmInline
value class UserId private constructor(
    val value: UUID,
) : Serializable {
    companion object {
        /**
         * Creates a new UserId from a UUID.
         */
        fun of(uuid: UUID): UserId = UserId(uuid)

        /**
         * Creates a new UserId from a string representation of a UUID.
         * @throws UserDomainException.Validation if the string is not a valid UUID
         */
        fun ofString(value: String): UserId =
            try {
                UserId(UUID.fromString(value))
            } catch (e: IllegalArgumentException) {
                throw UserDomainException.Validation.InvalidUserIdFormat(value)
            }

        /**
         * Generates a new random UserId.
         */
        fun generate(): UserId = UserId(UUID.randomUUID())

        fun fromUUID(value: UUID): UserId = UserId(value)
    }

    override fun toString(): String = value.toString()
}
