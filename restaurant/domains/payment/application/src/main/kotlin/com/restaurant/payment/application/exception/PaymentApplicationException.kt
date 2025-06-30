package com.restaurant.payment.application.exception

import com.restaurant.common.application.exception.ApplicationException
import com.restaurant.payment.application.error.PaymentApplicationErrorCodes

/**
 * Sealed class representing all possible application exceptions for the Payment domain. (Rule 68)
 */
sealed class PaymentApplicationException(
    override val errorCode: PaymentApplicationErrorCodes,
    message: String? = errorCode.message,
    cause: Throwable? = null,
) : ApplicationException(message, cause) {
    /**
     * External service related exceptions
     */
    sealed class ExternalService(
        override val errorCode: PaymentApplicationErrorCodes,
        message: String? = errorCode.message,
        cause: Throwable? = null,
    ) : PaymentApplicationException(errorCode, message, cause) {
        class PaymentGatewayUnavailable(
            cause: Throwable? = null,
        ) : ExternalService(
                PaymentApplicationErrorCodes.PAYMENT_GATEWAY_UNAVAILABLE,
                cause = cause,
            )

        class PaymentGatewayTimeout(
            cause: Throwable? = null,
        ) : ExternalService(
                PaymentApplicationErrorCodes.PAYMENT_GATEWAY_TIMEOUT,
                cause = cause,
            )

        class PaymentGatewayError(
            gatewayMessage: String,
            cause: Throwable? = null,
        ) : ExternalService(
                PaymentApplicationErrorCodes.PAYMENT_GATEWAY_ERROR,
                "Payment gateway error: $gatewayMessage",
                cause,
            )
    }

    /**
     * Validation related exceptions
     */
    sealed class Validation(
        override val errorCode: PaymentApplicationErrorCodes,
        message: String? = errorCode.message,
        cause: Throwable? = null,
    ) : PaymentApplicationException(errorCode, message, cause) {
        class InvalidPaymentRequest(
            details: String,
        ) : Validation(
                PaymentApplicationErrorCodes.INVALID_INPUT,
                "Invalid payment request: $details",
            )

        class InvalidRefundRequest(
            details: String,
        ) : Validation(
                PaymentApplicationErrorCodes.INVALID_INPUT,
                "Invalid refund request: $details",
            )

        class InvalidPaymentMethodRequest(
            details: String,
        ) : Validation(
                PaymentApplicationErrorCodes.PAYMENT_METHOD_VALIDATION_ERROR,
                "Invalid payment method request: $details",
            )
    }

    /**
     * Processing related exceptions
     */
    sealed class Processing(
        override val errorCode: PaymentApplicationErrorCodes,
        message: String? = errorCode.message,
        cause: Throwable? = null,
    ) : PaymentApplicationException(errorCode, message, cause) {
        class PaymentProcessingFailed(
            paymentId: String,
            reason: String,
            cause: Throwable? = null,
        ) : Processing(
                PaymentApplicationErrorCodes.PAYMENT_PROCESSING_FAILED,
                "Payment processing failed for ID: $paymentId, Reason: $reason",
                cause,
            )

        class RefundProcessingFailed(
            paymentId: String,
            reason: String,
            cause: Throwable? = null,
        ) : Processing(
                PaymentApplicationErrorCodes.REFUND_PROCESSING_FAILED,
                "Refund processing failed for payment ID: $paymentId, Reason: $reason",
                cause,
            )

        class PaymentMethodRegistrationFailed(
            userId: String,
            reason: String,
            cause: Throwable? = null,
        ) : Processing(
                PaymentApplicationErrorCodes.PAYMENT_METHOD_VALIDATION_ERROR,
                "Payment method registration failed for user ID: $userId, Reason: $reason",
                cause,
            )
    }

    /**
     * Concurrency related exceptions
     */
    sealed class Concurrency(
        override val errorCode: PaymentApplicationErrorCodes,
        message: String? = errorCode.message,
        cause: Throwable? = null,
    ) : PaymentApplicationException(errorCode, message, cause) {
        class PaymentConcurrentModification(
            paymentId: String,
            cause: Throwable? = null,
        ) : Concurrency(
                PaymentApplicationErrorCodes.OPTIMISTIC_LOCK_ERROR,
                "Payment was modified by another process: $paymentId",
                cause,
            )

        class PaymentMethodConcurrentModification(
            paymentMethodId: String,
            cause: Throwable? = null,
        ) : Concurrency(
                PaymentApplicationErrorCodes.OPTIMISTIC_LOCK_ERROR,
                "Payment method was modified by another process: $paymentMethodId",
                cause,
            )
    }

    /**
     * Resource not found exceptions
     */
    sealed class NotFound(
        override val errorCode: PaymentApplicationErrorCodes,
        message: String? = errorCode.message,
        cause: Throwable? = null,
    ) : PaymentApplicationException(errorCode, message, cause) {
        class PaymentNotFound(
            paymentId: String,
        ) : NotFound(
                PaymentApplicationErrorCodes.PAYMENT_NOT_FOUND,
                "Payment not found: $paymentId",
            )

        class PaymentMethodNotFound(
            paymentMethodId: String,
        ) : NotFound(
                PaymentApplicationErrorCodes.PAYMENT_METHOD_NOT_FOUND,
                "Payment method not found: $paymentMethodId",
            )

        class OrderNotFound(
            orderId: String,
        ) : NotFound(
                PaymentApplicationErrorCodes.ORDER_SERVICE_ERROR,
                "Order not found: $orderId",
            )
    }

    /**
     * System related exceptions
     */
    sealed class System(
        override val errorCode: PaymentApplicationErrorCodes,
        message: String? = errorCode.message,
        cause: Throwable? = null,
    ) : PaymentApplicationException(errorCode, message, cause) {
        class UnexpectedError(
            details: String? = null,
            cause: Throwable? = null,
        ) : System(
                PaymentApplicationErrorCodes.UNEXPECTED_ERROR,
                if (details != null) "Unexpected error: $details" else null,
                cause,
            )

        class DatabaseConnectionError(
            cause: Throwable? = null,
        ) : System(
                PaymentApplicationErrorCodes.SYSTEM_ERROR,
                cause = cause,
            )

        class MessagingError(
            details: String,
            cause: Throwable? = null,
        ) : System(
                PaymentApplicationErrorCodes.SYSTEM_ERROR,
                "Messaging error: $details",
                cause,
            )
    }
}
