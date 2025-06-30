package com.restaurant.payment.domain.vo

import com.restaurant.payment.domain.exception.PaymentDomainException

/**
 * Value object representing a credit card number.
 * This class ensures that the card number is valid and provides security through masking.
 */
@JvmInline
value class CardNumber private constructor(
    private val value: String,
) {
    companion object {
        private val CARD_NUMBER_PATTERN = Regex("^[0-9]{13,19}$")

        /**
         * Creates a new CardNumber from a string value.
         * @throws PaymentDomainException.Validation if the card number format is invalid
         */
        fun of(value: String): CardNumber {
            val cleanValue = value.replace("\\s".toRegex(), "") // Remove spaces

            if (!CARD_NUMBER_PATTERN.matches(cleanValue)) {
                throw PaymentDomainException.Validation.InvalidCardNumberFormat("Invalid card number format")
            }

            if (!isValidLuhn(cleanValue)) {
                throw PaymentDomainException.Validation.InvalidCardNumberFormat("Invalid card number checksum")
            }

            return CardNumber(cleanValue)
        }

        /**
         * Validates card number using Luhn algorithm
         */
        private fun isValidLuhn(cardNumber: String): Boolean {
            var sum = 0
            var isEven = false

            for (i in cardNumber.length - 1 downTo 0) {
                var digit = cardNumber[i].digitToInt()

                if (isEven) {
                    digit *= 2
                    if (digit > 9) {
                        digit -= 9
                    }
                }

                sum += digit
                isEven = !isEven
            }

            return sum % 10 == 0
        }
    }

    /**
     * Returns the actual card number value (for internal use only)
     */
    fun getValue(): String = value

    /**
     * Returns the last 4 digits of the card number
     */
    fun getLastFourDigits(): String = value.takeLast(4)

    /**
     * Returns the card type based on the number
     */
    fun getCardType(): String =
        when {
            value.startsWith("4") -> "VISA"
            value.startsWith("5") || value.startsWith("2") -> "MASTERCARD"
            value.startsWith("3") -> "AMEX"
            else -> "UNKNOWN"
        }

    /**
     * Returns a masked version of the card number for display purposes
     */
    override fun toString(): String = "**** **** **** ${getLastFourDigits()}"
}
