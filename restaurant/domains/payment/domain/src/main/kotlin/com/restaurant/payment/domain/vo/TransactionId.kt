package com.restaurant.payment.domain.vo

import com.restaurant.payment.domain.exception.PaymentDomainException

/**
 * Value object representing a transaction identifier from external payment gateway.
 * This class ensures that the transaction ID is valid.
 */
@JvmInline
value class TransactionId private constructor(
    val value: String,
) {
    companion object {
        private const val MIN_LENGTH = 1
        private const val MAX_LENGTH = 100
        private val TRANSACTION_ID_PATTERN = Regex("^[a-zA-Z0-9_\\-]+$")

        /**
         * Creates a new TransactionId from a string value.
         * @throws PaymentDomainException.Validation if the transaction ID format is invalid
         */
        fun of(value: String): TransactionId {
            val trimmedValue = value.trim()

            if (trimmedValue.length < MIN_LENGTH || trimmedValue.length > MAX_LENGTH) {
                throw PaymentDomainException.Validation.InvalidTransactionIdFormat(
                    "Transaction ID length must be between $MIN_LENGTH and $MAX_LENGTH characters: $value",
                )
            }

            if (!TRANSACTION_ID_PATTERN.matches(trimmedValue)) {
                throw PaymentDomainException.Validation.InvalidTransactionIdFormat(
                    "Transaction ID contains invalid characters. Only alphanumeric, underscore, and hyphen are allowed: $value",
                )
            }

            return TransactionId(trimmedValue)
        }
    }

    override fun toString(): String = value
}
