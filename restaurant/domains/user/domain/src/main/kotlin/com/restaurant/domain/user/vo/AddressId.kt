package com.restaurant.domain.user.vo

import java.util.UUID

data class AddressId private constructor(
    val value: UUID,
) {
    companion object {
        fun generate(): AddressId = AddressId(UUID.randomUUID())

        fun fromString(value: String): AddressId = AddressId(UUID.fromString(value))

        fun of(value: UUID): AddressId = AddressId(value)
    }

    override fun toString(): String = value.toString()
}
