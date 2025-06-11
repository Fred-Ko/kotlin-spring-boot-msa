package com.restaurant.account.domain.vo

import com.restaurant.account.domain.exception.AccountDomainException
import java.math.BigDecimal
import java.io.Serializable as JavaSerializable

@JvmInline
value class Balance private constructor(
    val value: BigDecimal,
) : JavaSerializable,
    Comparable<Balance> {
    companion object {
        val ZERO = Balance(BigDecimal.ZERO)

        fun of(value: Long): Balance {
            if (value < 0) {
                throw AccountDomainException.Validation.InvalidInitialBalance(value)
            }
            return Balance(BigDecimal.valueOf(value))
        }

        fun of(value: BigDecimal): Balance {
            if (value < BigDecimal.ZERO) {
                throw AccountDomainException.Validation.InvalidInitialBalance(value.toLong())
            }
            return Balance(value)
        }
    }

    operator fun plus(other: Balance): Balance = Balance(this.value.add(other.value))

    operator fun minus(other: Balance): Balance {
        val result = this.value.subtract(other.value)
        if (result < BigDecimal.ZERO) {
            throw AccountDomainException.Operation.InsufficientFunds(
                "N/A",
                this.value.toLong(),
                other.value.toLong(),
            )
        }
        return Balance(result)
    }

    override fun compareTo(other: Balance): Int = this.value.compareTo(other.value)

    override fun toString(): String = value.toPlainString()
}
