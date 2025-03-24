package com.restaurant.domain.user.vo

@JvmInline
value class UserId(val value: Long) {
    override fun toString(): String = value.toString()
}
