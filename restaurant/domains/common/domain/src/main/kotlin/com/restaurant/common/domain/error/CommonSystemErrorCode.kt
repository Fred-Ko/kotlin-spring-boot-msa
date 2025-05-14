package com.restaurant.common.domain.error

import org.springframework.http.HttpStatus

/**
 * Common system-level error codes.
 */
enum class CommonSystemErrorCode(
    override val code: String,
    override val message: String,
    override val defaultHttpStatus: HttpStatus,
) : ErrorCode {
    INTERNAL_SERVER_ERROR("COMMON-SYSTEM-001", "Internal server error", HttpStatus.INTERNAL_SERVER_ERROR),
    INVALID_REQUEST("COMMON-SYSTEM-002", "Invalid request", HttpStatus.BAD_REQUEST),
    RESOURCE_NOT_FOUND("COMMON-SYSTEM-003", "Resource not found", HttpStatus.NOT_FOUND),
    UNAUTHORIZED("COMMON-SYSTEM-004", "Unauthorized", HttpStatus.UNAUTHORIZED),
    FORBIDDEN("COMMON-SYSTEM-005", "Forbidden", HttpStatus.FORBIDDEN),
    VALIDATION_ERROR("COMMON-SYSTEM-006", "Validation error", HttpStatus.BAD_REQUEST),
    CONFLICT("COMMON-SYSTEM-007", "Conflict", HttpStatus.CONFLICT),
    TOO_MANY_REQUESTS("COMMON-SYSTEM-008", "Too many requests", HttpStatus.TOO_MANY_REQUESTS),
    SERVICE_UNAVAILABLE("COMMON-SYSTEM-009", "Service unavailable", HttpStatus.SERVICE_UNAVAILABLE),
    OPTIMISTIC_LOCK_ERROR("COMMON-SYSTEM-010", "Optimistic lock error", HttpStatus.CONFLICT)
}
