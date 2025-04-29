package com.restaurant.user.domain.vo
import com.restaurant.user.domain.exception.UserDomainException
import java.io.Serializable
import java.util.UUID

@JvmInline
value class AddressId private constructor(
    val value: UUID,
) : Serializable {
    companion object {
        fun generate(): AddressId = AddressId(UUID.randomUUID())

        fun ofString(value: String): AddressId =
            try {
                AddressId(UUID.fromString(value))
            } catch (e: IllegalArgumentException) {
                throw UserDomainException.Validation.InvalidAddressIdFormat(value)
            }

        fun of(value: UUID): AddressId = AddressId(value)

        fun fromUUID(value: UUID): AddressId = AddressId(value)
    }

    override fun toString(): String = value.toString()
}
