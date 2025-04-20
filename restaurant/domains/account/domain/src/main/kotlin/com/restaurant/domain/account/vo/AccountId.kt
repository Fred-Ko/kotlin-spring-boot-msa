package com.restaurant.domain.account.vo

data class AccountId private constructor(
    val value: Long,
) {
    init {
        require(value >= 0L) { "AccountId must be greater than or equal to 0, but was: $value" }
    }

    companion object {
        fun of(value: Long): AccountId = AccountId(value)
    }

    override fun toString(): String = "AccountId(value=$value)"
}
