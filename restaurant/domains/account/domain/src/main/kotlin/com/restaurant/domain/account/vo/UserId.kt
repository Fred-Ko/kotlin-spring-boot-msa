package com.restaurant.domain.account.vo

@JvmInline
value class UserId(
    val value: Long,
) {
    companion object {
        fun of(value: Long): UserId = UserId(value)
    }
}
