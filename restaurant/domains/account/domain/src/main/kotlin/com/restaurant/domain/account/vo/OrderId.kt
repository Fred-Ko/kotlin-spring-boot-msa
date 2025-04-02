package com.restaurant.domain.account.vo

@JvmInline
value class OrderId(
    val value: String,
) {
    companion object {
        fun of(value: String): OrderId = OrderId(value)
    }
}
