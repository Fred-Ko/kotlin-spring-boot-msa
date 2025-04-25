package com.restaurant.domain.account.vo

data class UserId private constructor(
    val value: Long,
) {
    init {
        require(value > 0L) { "UserId must be greater than 0, but was: $value" }
    }

    companion object {
        fun of(value: Long): UserId = UserId(value)
    }

    override fun toString(): String = "UserId(value=$value)"
}
