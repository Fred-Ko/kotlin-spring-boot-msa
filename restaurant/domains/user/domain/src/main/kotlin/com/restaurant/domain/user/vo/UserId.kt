package com.restaurant.domain.user.vo

import java.util.UUID

data class UserId private constructor(
    val value: UUID,
) {
    companion object {
        fun of(value: UUID): UserId = UserId(value)

        fun generate(): UserId = UserId(UUID.randomUUID())

        fun fromString(value: String): UserId = UserId(UUID.fromString(value))
    }

    override fun toString(): String = value.toString()
}
