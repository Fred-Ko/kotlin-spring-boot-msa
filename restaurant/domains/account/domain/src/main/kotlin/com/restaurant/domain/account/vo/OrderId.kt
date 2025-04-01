package com.restaurant.domain.account.vo

@JvmInline
value class OrderId(
    val value: Long,
) {
    companion object {
        fun of(value: Long): OrderId = OrderId(value)
    }
}
