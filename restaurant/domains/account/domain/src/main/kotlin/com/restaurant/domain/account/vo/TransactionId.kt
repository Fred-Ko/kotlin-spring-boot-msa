package com.restaurant.domain.account.vo

import kotlin.ConsistentCopyVisibility

@ConsistentCopyVisibility
data class TransactionId private constructor(
    val value: Long,
) {
    init {
        require(value > 0) { "TransactionId는 0보다 커야 합니다." }
    }

    companion object {
        fun of(value: Long): TransactionId = TransactionId(value)
    }

    override fun toString(): String = value.toString()
}
