package com.restaurant.domain.account.vo

import kotlin.ConsistentCopyVisibility

@ConsistentCopyVisibility
data class AccountId private constructor(
    val value: Long,
) {
    init {
        require(value > 0) { "AccountId는 0보다 커야 합니다." }
    }

    companion object {
        fun of(value: Long): AccountId = AccountId(value)
    }

    override fun toString(): String = value.toString()
}
