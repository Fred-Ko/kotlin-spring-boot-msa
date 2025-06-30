package com.restaurant.payment.domain.vo

import com.restaurant.payment.domain.exception.PaymentDomainException
import java.math.BigDecimal
import java.math.RoundingMode

/**
 * Value object representing a payment amount.
 * This class ensures that the amount is valid and handles monetary calculations properly.
 */
@JvmInline
value class Amount private constructor(
    val value: BigDecimal,
) {
    companion object {
        private val ZERO = BigDecimal.ZERO.setScale(2, RoundingMode.HALF_UP)
        private val MAX_AMOUNT = BigDecimal("999999999.99")

        /**
         * Creates a new Amount from a BigDecimal value.
         * @throws PaymentDomainException.Validation if the amount is invalid
         */
        fun of(value: BigDecimal): Amount {
            val scaledValue = value.setScale(2, RoundingMode.HALF_UP)

            if (scaledValue < ZERO) {
                throw PaymentDomainException.Validation.InvalidAmountFormat("Amount cannot be negative: $scaledValue")
            }
            if (scaledValue > MAX_AMOUNT) {
                throw PaymentDomainException.Validation.InvalidAmountFormat("Amount exceeds maximum allowed: $scaledValue")
            }

            return Amount(scaledValue)
        }

        /**
         * Creates a new Amount from a double value.
         * @throws PaymentDomainException.Validation if the amount is invalid
         */
        fun of(value: Double): Amount = of(BigDecimal.valueOf(value))

        /**
         * Creates a new Amount from a string value.
         * @throws PaymentDomainException.Validation if the amount format is invalid
         */
        fun of(value: String): Amount {
            try {
                val bigDecimalValue = BigDecimal(value)
                return of(bigDecimalValue)
            } catch (e: NumberFormatException) {
                throw PaymentDomainException.Validation.InvalidAmountFormat("Invalid amount format: $value")
            }
        }

        /**
         * Creates a zero amount.
         */
        fun zero(): Amount = Amount(ZERO)
    }

    /**
     * Adds another amount to this amount.
     */
    fun add(other: Amount): Amount = Amount(this.value.add(other.value))

    /**
     * Subtracts another amount from this amount.
     */
    fun subtract(other: Amount): Amount {
        val result = this.value.subtract(other.value)
        if (result < ZERO) {
            throw PaymentDomainException.Payment.InvalidAmount("Subtraction result cannot be negative")
        }
        return Amount(result)
    }

    /**
     * Checks if this amount is greater than another amount.
     */
    fun isGreaterThan(other: Amount): Boolean = this.value > other.value

    /**
     * Checks if this amount is greater than or equal to another amount.
     */
    fun isGreaterThanOrEqual(other: Amount): Boolean = this.value >= other.value

    /**
     * Checks if this amount is less than another amount.
     */
    fun isLessThan(other: Amount): Boolean = this.value < other.value

    /**
     * Checks if this amount is zero.
     */
    fun isZero(): Boolean = this.value.compareTo(ZERO) == 0

    /**
     * Checks if this amount is positive.
     */
    fun isPositive(): Boolean = this.value > ZERO

    override fun toString(): String = value.toString()
}
