package com.restaurant.domain.account.vo

import java.math.BigDecimal

@JvmInline
value class Money(
    val value: BigDecimal,
) {
    operator fun plus(other: Money): Money = Money(this.value.add(other.value))

    operator fun minus(other: Money): Money = Money(this.value.subtract(other.value))

    fun isGreaterThanOrEqual(other: Money): Boolean = this.value >= other.value

    fun isZero(): Boolean = value.compareTo(BigDecimal.ZERO) == 0

    companion object {
        fun of(value: BigDecimal): Money = Money(value)

        fun of(value: Long): Money = Money(BigDecimal.valueOf(value))

        fun of(value: Int): Money = Money(BigDecimal.valueOf(value.toLong()))

        val ZERO: Money = Money(BigDecimal.ZERO)
    }
}
