package com.restaurant.shared.outbox.infrastructure.error

import com.restaurant.common.core.error.ErrorCode
import org.springframework.http.HttpStatus

/**
 * Error codes specific to the Outbox module's infrastructure layer.
 */
enum class OutboxInfrastructureErrorCode( // Rule 67
    override val code: String,
    override val message: String,
    override val status: HttpStatus,
) : ErrorCode {
    DATABASE_OPERATION_FAILED(
        "OUTBOX-INFRA-001",
        "Failed to perform database operation on outbox event.",
        HttpStatus.INTERNAL_SERVER_ERROR,
    ),
    SERIALIZATION_FAILED("OUTBOX-INFRA-002", "Failed to serialize outbox event payload.", HttpStatus.INTERNAL_SERVER_ERROR),
    DESERIALIZATION_FAILED("OUTBOX-INFRA-003", "Failed to deserialize outbox event payload.", HttpStatus.INTERNAL_SERVER_ERROR),
    // Add more specific infrastructure errors if needed
}
