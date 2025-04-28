package com.restaurant.common.error

/**
 * Common system-level error codes.
 */
enum class CommonSystemErrorCode(
    override val code: String,
    override val message: String,
) : ErrorCode {
    INTERNAL_ERROR("COMMON-SYS-001", "An internal system error occurred"),
    INVALID_REQUEST("COMMON-SYS-002", "The request is invalid"),
    RESOURCE_NOT_FOUND("COMMON-SYS-003", "The requested resource was not found"),
    UNAUTHORIZED("COMMON-SYS-004", "Unauthorized access"),
    FORBIDDEN("COMMON-SYS-005", "Access forbidden"),
    VALIDATION_ERROR("COMMON-SYS-006", "Validation error"),
    OPTIMISTIC_LOCK_ERROR("COMMON-SYS-007", "The resource was modified by another request"),
    OUTBOX_ERROR("COMMON-SYS-008", "Error processing outbox message"),
    EXTERNAL_SERVICE_ERROR("COMMON-SYS-009", "External service error"),
}
