package com.restaurant.domain.account.vo

import java.math.BigDecimal

@JvmInline
value class Money private constructor(
    val amount: BigDecimal,
) {
    operator fun plus(other: Money): Money = Money(this.amount.add(other.amount))

    operator fun minus(other: Money): Money = Money(this.amount.subtract(other.amount))

    fun isGreaterThanOrEqual(other: Money): Boolean = this.amount >= other.amount

    fun isZero(): Boolean = amount.compareTo(BigDecimal.ZERO) == 0

    companion object {
        val ZERO = Money(BigDecimal.ZERO)

        fun of(amount: BigDecimal): Money {
            require(amount >= BigDecimal.ZERO) { "금액은 0 이상이어야 합니다." }
            return Money(amount)
        }

        fun of(amount: Long): Money = of(BigDecimal.valueOf(amount))

        fun of(amount: Int): Money = of(BigDecimal.valueOf(amount.toLong()))
    }
}
