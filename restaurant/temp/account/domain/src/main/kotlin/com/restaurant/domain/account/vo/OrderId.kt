package com.restaurant.domain.account.vo

data class OrderId private constructor(
    val value: String,
) {
    init {
        require(value.isNotBlank()) { "OrderId는 빈 값일 수 없습니다." }
    }

    companion object {
        fun of(value: String): OrderId = OrderId(value)
    }

    override fun toString(): String = value
}
