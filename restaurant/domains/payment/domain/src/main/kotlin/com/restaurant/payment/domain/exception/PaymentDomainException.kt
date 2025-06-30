package com.restaurant.payment.domain.exception

import com.restaurant.common.domain.exception.DomainException
import com.restaurant.payment.domain.error.PaymentDomainErrorCodes

/**
 * Sealed class representing all possible domain exceptions for the Payment aggregate. (Rule 68)
 */
sealed class PaymentDomainException(
    override val errorCode: PaymentDomainErrorCodes,
    message: String? = errorCode.message, // String? 타입으로 변경하고, null일 경우 errorCode.message 사용
    cause: Throwable? = null,
) : DomainException(message, cause) { // DomainException 생성자 변경에 따라 수정
    /**
     * Validation-related exceptions
     */
    sealed class Validation(
        override val errorCode: PaymentDomainErrorCodes,
        message: String = errorCode.message,
        cause: Throwable? = null,
    ) : PaymentDomainException(errorCode, message, cause) {
        class InvalidPaymentIdFormat(
            message: String,
        ) : Validation(
                PaymentDomainErrorCodes.INVALID_PAYMENT_ID_FORMAT,
                message,
            )

        class InvalidPaymentMethodIdFormat(
            message: String,
        ) : Validation(
                PaymentDomainErrorCodes.INVALID_PAYMENT_METHOD_ID_FORMAT,
                message,
            )

        class InvalidOrderIdFormat(
            message: String,
        ) : Validation(
                PaymentDomainErrorCodes.INVALID_ORDER_ID_FORMAT,
                message,
            )

        class InvalidUserIdFormat(
            message: String,
        ) : Validation(
                PaymentDomainErrorCodes.INVALID_USER_ID_FORMAT,
                message,
            )

        class InvalidAmountFormat(
            message: String,
        ) : Validation(
                PaymentDomainErrorCodes.INVALID_AMOUNT_FORMAT,
                message,
            )

        class InvalidCardNumberFormat(
            message: String,
        ) : Validation(
                PaymentDomainErrorCodes.INVALID_CARD_NUMBER_FORMAT,
                message,
            )

        class InvalidCardExpiryFormat(
            message: String,
        ) : Validation(
                PaymentDomainErrorCodes.INVALID_CARD_EXPIRY_FORMAT,
                message,
            )

        class InvalidCardCvvFormat(
            message: String,
        ) : Validation(
                PaymentDomainErrorCodes.INVALID_CARD_CVV_FORMAT,
                message,
            )

        class InvalidTransactionIdFormat(
            value: String,
        ) : Validation(
                PaymentDomainErrorCodes.INVALID_TRANSACTION_ID_FORMAT,
                "Invalid transaction ID format: $value",
            )
    }

    /**
     * Payment-related exceptions
     */
    sealed class Payment(
        override val errorCode: PaymentDomainErrorCodes,
        message: String? = errorCode.message,
        cause: Throwable? = null,
    ) : PaymentDomainException(errorCode, message, cause) {
        class NotFound(
            paymentId: String,
        ) : Payment(
                PaymentDomainErrorCodes.PAYMENT_NOT_FOUND,
                "Payment not found with ID: $paymentId",
            )

        class AlreadyApproved(
            paymentId: String,
        ) : Payment(
                PaymentDomainErrorCodes.PAYMENT_ALREADY_APPROVED,
                "Payment already approved with ID: $paymentId",
            )

        class AlreadyFailed(
            paymentId: String,
        ) : Payment(
                PaymentDomainErrorCodes.PAYMENT_ALREADY_FAILED,
                "Payment already failed with ID: $paymentId",
            )

        class AlreadyRefunded(
            paymentId: String,
        ) : Payment(
                PaymentDomainErrorCodes.PAYMENT_ALREADY_REFUNDED,
                "Payment already refunded with ID: $paymentId",
            )

        class CannotBeRefunded(
            paymentId: String,
        ) : Payment(
                PaymentDomainErrorCodes.PAYMENT_CANNOT_BE_REFUNDED,
                "Payment cannot be refunded with ID: $paymentId",
            )

        class InsufficientRefundAmount(
            requestedAmount: String,
            availableAmount: String,
        ) : Payment(
                PaymentDomainErrorCodes.INSUFFICIENT_REFUND_AMOUNT,
                "Insufficient refund amount. Requested: $requestedAmount, Available: $availableAmount",
            )

        class InvalidAmount(
            amount: String,
        ) : Payment(
                PaymentDomainErrorCodes.INVALID_PAYMENT_AMOUNT,
                "Invalid payment amount: $amount",
            )

        class ProcessingError(
            paymentId: String,
            reason: String,
        ) : Payment(
                PaymentDomainErrorCodes.PAYMENT_PROCESSING_ERROR,
                "Payment processing error for ID: $paymentId, Reason: $reason",
            )

        class RefundProcessingError(
            paymentId: String,
            reason: String,
        ) : Payment(
                PaymentDomainErrorCodes.REFUND_PROCESSING_ERROR,
                "Refund processing error for payment ID: $paymentId, Reason: $reason",
            )

        class ExternalGatewayError(
            gatewayMessage: String,
        ) : Payment(
                PaymentDomainErrorCodes.EXTERNAL_PAYMENT_GATEWAY_ERROR,
                "External payment gateway error: $gatewayMessage",
            )
    }

    /**
     * PaymentMethod-related exceptions
     */
    sealed class PaymentMethod(
        override val errorCode: PaymentDomainErrorCodes,
        message: String? = errorCode.message,
        cause: Throwable? = null,
    ) : PaymentDomainException(errorCode, message, cause) {
        class NotFound(
            paymentMethodId: String,
        ) : PaymentMethod(
                PaymentDomainErrorCodes.PAYMENT_METHOD_NOT_FOUND,
                "Payment method not found with ID: $paymentMethodId",
            )

        class AlreadyExists(
            cardNumber: String,
        ) : PaymentMethod(
                PaymentDomainErrorCodes.PAYMENT_METHOD_ALREADY_EXISTS,
                "Payment method already exists with card number: ${cardNumber.takeLast(4).padStart(cardNumber.length, '*')}",
            )

        class IdMismatch(
            existingId: String,
            newId: String,
        ) : PaymentMethod(
                PaymentDomainErrorCodes.PAYMENT_METHOD_ID_MISMATCH,
                "Payment method ID mismatch: expected $existingId, got $newId",
            )

        class LimitExceeded(
            limit: Int,
        ) : PaymentMethod(
                PaymentDomainErrorCodes.PAYMENT_METHOD_LIMIT_EXCEEDED,
                "Cannot add more payment methods, limit is $limit",
            )

        class CannotDeleteDefault(
            message: String = "Cannot remove the default payment method.",
        ) : PaymentMethod(
                PaymentDomainErrorCodes.DEFAULT_PAYMENT_METHOD_CANNOT_BE_DELETED,
                message,
            )

        class CannotDeleteLast(
            message: String = "Cannot remove the last payment method.",
        ) : PaymentMethod(
                PaymentDomainErrorCodes.CANNOT_DELETE_LAST_PAYMENT_METHOD,
                message,
            )
    }

    class PersistenceError(
        override val errorCode: PaymentDomainErrorCodes = PaymentDomainErrorCodes.PERSISTENCE_ERROR,
        message: String? = errorCode.message,
        cause: Throwable? = null,
    ) : PaymentDomainException(errorCode, message, cause)
} 