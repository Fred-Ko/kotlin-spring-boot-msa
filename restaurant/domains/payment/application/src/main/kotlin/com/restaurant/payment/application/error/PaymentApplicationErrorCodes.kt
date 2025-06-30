package com.restaurant.payment.application.error

import com.restaurant.common.domain.error.ErrorCode

/**
 * Payment Application 레이어 관련 에러 코드 Enum (Rule 67)
 * Application 레이어에서 발생하는 기술적 오류에 대한 ErrorCode 정의
 */
enum class PaymentApplicationErrorCodes(
    override val code: String,
    override val message: String,
) : ErrorCode {
    // 일반적인 Application 오류 (PAYMENT-APPLICATION-001~099)
    BAD_REQUEST("PAYMENT-APPLICATION-001", "Bad request received by application."),
    INVALID_INPUT("PAYMENT-APPLICATION-002", "Invalid input provided to application."),
    AUTHENTICATION_FAILED("PAYMENT-APPLICATION-003", "Authentication failed."),
    EXTERNAL_SERVICE_ERROR("PAYMENT-APPLICATION-004", "External service communication error."),
    UNEXPECTED_ERROR("PAYMENT-APPLICATION-500", "An unexpected error occurred in the application."),

    // 결제 처리 관련 Application 오류 (PAYMENT-APPLICATION-101~199)
    PAYMENT_GATEWAY_ERROR("PAYMENT-APPLICATION-101", "Payment gateway communication error."),
    PAYMENT_GATEWAY_TIMEOUT("PAYMENT-APPLICATION-102", "Payment gateway request timeout."),
    PAYMENT_GATEWAY_UNAVAILABLE("PAYMENT-APPLICATION-103", "Payment gateway is currently unavailable."),
    PAYMENT_PROCESSING_FAILED("PAYMENT-APPLICATION-104", "Payment processing failed due to technical error."),
    PAYMENT_NOT_FOUND("PAYMENT-APPLICATION-105", "Payment not found."),
    PAYMENT_METHOD_NOT_FOUND("PAYMENT-APPLICATION-106", "Payment method not found."),

    // 외부 서비스 연동 오류 (PAYMENT-APPLICATION-201~299)
    ORDER_SERVICE_ERROR("PAYMENT-APPLICATION-201", "Order service communication error."),
    ORDER_SERVICE_TIMEOUT("PAYMENT-APPLICATION-202", "Order service request timeout."),
    USER_SERVICE_ERROR("PAYMENT-APPLICATION-203", "User service communication error."),
    USER_SERVICE_TIMEOUT("PAYMENT-APPLICATION-204", "User service request timeout."),
    NOTIFICATION_SERVICE_ERROR("PAYMENT-APPLICATION-205", "Notification service communication error."),

    // 환불 처리 관련 Application 오류 (PAYMENT-APPLICATION-301~399)
    REFUND_GATEWAY_ERROR("PAYMENT-APPLICATION-301", "Refund gateway communication error."),
    REFUND_GATEWAY_TIMEOUT("PAYMENT-APPLICATION-302", "Refund gateway request timeout."),
    REFUND_PROCESSING_FAILED("PAYMENT-APPLICATION-303", "Refund processing failed due to technical error."),

    // 결제 수단 관리 관련 Application 오류 (PAYMENT-APPLICATION-401~499)
    PAYMENT_METHOD_VALIDATION_ERROR("PAYMENT-APPLICATION-401", "Payment method validation failed."),
    PAYMENT_METHOD_ENCRYPTION_ERROR("PAYMENT-APPLICATION-402", "Payment method encryption failed."),
    PAYMENT_METHOD_DECRYPTION_ERROR("PAYMENT-APPLICATION-403", "Payment method decryption failed."),

    // 동시성 및 트랜잭션 오류 (PAYMENT-APPLICATION-501~599)
    OPTIMISTIC_LOCK_ERROR("PAYMENT-APPLICATION-501", "Optimistic lock conflict occurred."),
    TRANSACTION_ROLLBACK_ERROR("PAYMENT-APPLICATION-502", "Transaction rollback occurred."),
    DEADLOCK_ERROR("PAYMENT-APPLICATION-503", "Database deadlock occurred."),

    // 시스템 리소스 오류 (PAYMENT-APPLICATION-601~699)
    MEMORY_LIMIT_EXCEEDED("PAYMENT-APPLICATION-601", "Memory limit exceeded during processing."),
    PROCESSING_TIMEOUT("PAYMENT-APPLICATION-602", "Processing timeout occurred."),
    RATE_LIMIT_EXCEEDED("PAYMENT-APPLICATION-603", "Rate limit exceeded for payment processing."),

    // 시스템 오류 (PAYMENT-APPLICATION-999)
    SYSTEM_ERROR("PAYMENT-APPLICATION-999", "처리 중 오류가 발생했습니다."),
}
