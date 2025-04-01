package com.restaurant.domain.account.vo

@JvmInline
value class AccountId(
    val value: Long,
) {
    companion object {
        fun of(value: Long): AccountId = AccountId(value)
    }
}
