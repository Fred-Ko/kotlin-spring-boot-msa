package com.restaurant.payment.domain.vo

import com.restaurant.payment.domain.exception.PaymentDomainException

/**
 * Value object representing a bank account number.
 */
@JvmInline
value class AccountNumber private constructor(
    val value: String,
) {
    companion object {
        private val REGEX = "^[0-9-]+$".toRegex()
        private const val MIN_LENGTH = 10
        private const val MAX_LENGTH = 20

        fun of(value: String): AccountNumber {
            val trimmedValue = value.trim()
            if (trimmedValue.length < MIN_LENGTH || trimmedValue.length > MAX_LENGTH || !REGEX.matches(trimmedValue)) {
                throw PaymentDomainException.Validation.InvalidAccountNumberFormat(
                    "Account number must be between $MIN_LENGTH and $MAX_LENGTH characters and contain only digits and hyphens.",
                )
            }
            return AccountNumber(trimmedValue)
        }
    }

    /**
     * Returns a masked version of the account number, e.g., "********1234"
     */
    override fun toString(): String {
        val lastFour = value.takeLast(4)
        return lastFour.padStart(value.length, '*')
    }

    fun getUnmaskedValue(): String = value
}
