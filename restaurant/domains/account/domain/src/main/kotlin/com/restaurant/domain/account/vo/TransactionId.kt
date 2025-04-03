package com.restaurant.domain.account.vo

@JvmInline
value class TransactionId(
    val value: Long,
) {
    companion object {
        fun of(value: Long): TransactionId = TransactionId(value)
    }
}
