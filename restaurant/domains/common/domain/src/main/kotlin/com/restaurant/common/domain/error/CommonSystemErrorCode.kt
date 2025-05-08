package com.restaurant.common.domain.error

/**
 * Common system-level error codes.
 */
enum class CommonSystemErrorCode(
    override val code: String,
    override val message: String,
) : ErrorCode {
    INTERNAL_SERVER_ERROR("COMMON-001", "Internal server error"),
    INVALID_REQUEST("COMMON-002", "Invalid request"),
    RESOURCE_NOT_FOUND("COMMON-003", "Resource not found"),
    UNAUTHORIZED("COMMON-004", "Unauthorized"),
    FORBIDDEN("COMMON-005", "Forbidden"),
    VALIDATION_ERROR("COMMON-006", "Validation error"),
    CONFLICT("COMMON-007", "Conflict"),
    TOO_MANY_REQUESTS("COMMON-008", "Too many requests"),
    SERVICE_UNAVAILABLE("COMMON-009", "Service unavailable"),
}
