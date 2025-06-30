package com.restaurant.payment.domain.vo

import com.restaurant.payment.domain.exception.PaymentDomainException

/**
 * Value object representing a credit card CVV.
 * This class ensures that the CVV is valid and provides security through masking.
 */
@JvmInline
value class CardCvv private constructor(
    private val value: String,
) {
    companion object {
        private val CVV_PATTERN = Regex("^[0-9]{3,4}$")

        /**
         * Creates a new CardCvv from a string value.
         * @throws PaymentDomainException.Validation if the CVV format is invalid
         */
        fun of(value: String): CardCvv {
            val trimmedValue = value.trim()

            if (!CVV_PATTERN.matches(trimmedValue)) {
                throw PaymentDomainException.Validation.InvalidCardCvvFormat("Invalid CVV format. Expected 3-4 digits: $value")
            }

            return CardCvv(trimmedValue)
        }
    }

    /**
     * Returns the actual CVV value (for internal use only)
     */
    fun getValue(): String = value

    /**
     * Returns the length of the CVV
     */
    fun getLength(): Int = value.length

    /**
     * Checks if this is a 3-digit CVV (Visa, MasterCard)
     */
    fun isThreeDigit(): Boolean = value.length == 3

    /**
     * Checks if this is a 4-digit CVV (American Express)
     */
    fun isFourDigit(): Boolean = value.length == 4

    /**
     * Returns a masked version of the CVV for security purposes
     */
    override fun toString(): String = "*".repeat(value.length)
}
