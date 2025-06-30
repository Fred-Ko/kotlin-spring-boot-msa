package com.restaurant.payment.domain.vo

import com.restaurant.payment.domain.exception.PaymentDomainException
import java.time.LocalDate
import java.time.YearMonth
import java.time.format.DateTimeFormatter
import java.time.format.DateTimeParseException

/**
 * Value object representing a credit card expiry date.
 * This class ensures that the expiry date is valid and not expired.
 */
@JvmInline
value class CardExpiry private constructor(
    private val value: String, // Format: MM/YY
) {
    companion object {
        private val EXPIRY_PATTERN = Regex("^(0[1-9]|1[0-2])/([0-9]{2})$")
        private val FORMATTER = DateTimeFormatter.ofPattern("MM/yy")

        /**
         * Creates a new CardExpiry from a string value.
         * @throws PaymentDomainException.Validation if the expiry format is invalid or expired
         */
        fun of(value: String): CardExpiry {
            val trimmedValue = value.trim()

            if (!EXPIRY_PATTERN.matches(trimmedValue)) {
                throw PaymentDomainException.Validation.InvalidCardExpiryFormat("Invalid card expiry format. Expected MM/YY format: $value")
            }

            try {
                val yearMonth = YearMonth.parse(trimmedValue, FORMATTER)
                val currentYearMonth = YearMonth.now()

                if (yearMonth.isBefore(currentYearMonth)) {
                    throw PaymentDomainException.Validation.InvalidCardExpiryFormat("Card expiry date is in the past: $value")
                }

                return CardExpiry(trimmedValue)
            } catch (e: DateTimeParseException) {
                throw PaymentDomainException.Validation.InvalidCardExpiryFormat("Invalid card expiry date format: $value")
            }
        }

        /**
         * Creates a new CardExpiry from month and year values.
         * @throws PaymentDomainException.Validation if the expiry is invalid or expired
         */
        fun of(
            month: Int,
            year: Int,
        ): CardExpiry {
            if (month < 1 || month > 12) {
                throw PaymentDomainException.Validation.InvalidCardExpiryFormat("Invalid month: $month")
            }

            val formattedMonth = String.format("%02d", month)
            val formattedYear = String.format("%02d", year % 100)

            return of("$formattedMonth/$formattedYear")
        }
    }

    /**
     * Returns the expiry date value
     */
    fun getValue(): String = value

    /**
     * Returns the month part of the expiry date
     */
    fun getMonth(): Int = value.substring(0, 2).toInt()

    /**
     * Returns the year part of the expiry date (2-digit)
     */
    fun getYear(): Int = value.substring(3, 5).toInt()

    /**
     * Returns the full year (4-digit)
     */
    fun getFullYear(): Int {
        val twoDigitYear = getYear()
        val currentYear = LocalDate.now().year
        val currentCentury = (currentYear / 100) * 100

        return if (twoDigitYear + currentCentury >= currentYear) {
            twoDigitYear + currentCentury
        } else {
            twoDigitYear + currentCentury + 100
        }
    }

    /**
     * Checks if the card is expired
     */
    fun isExpired(): Boolean {
        val yearMonth = YearMonth.of(getFullYear(), getMonth())
        return yearMonth.isBefore(YearMonth.now())
    }

    override fun toString(): String = value
}
