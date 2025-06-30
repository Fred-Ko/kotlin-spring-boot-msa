package com.restaurant.payment.domain.vo

import com.restaurant.payment.domain.exception.PaymentDomainException

/**
 * Value object representing a bank name.
 */
@JvmInline
value class BankName private constructor(
    val value: String,
) {
    companion object {
        private const val MIN_LENGTH = 2
        private const val MAX_LENGTH = 50

        fun of(value: String): BankName {
            val trimmedValue = value.trim()
            if (trimmedValue.length < MIN_LENGTH || trimmedValue.length > MAX_LENGTH) {
                throw PaymentDomainException.Validation.InvalidAmountFormat(
                    "Bank name length must be between $MIN_LENGTH and $MAX_LENGTH characters.",
                )
            }
            return BankName(trimmedValue)
        }
    }

    override fun toString(): String = value
}
