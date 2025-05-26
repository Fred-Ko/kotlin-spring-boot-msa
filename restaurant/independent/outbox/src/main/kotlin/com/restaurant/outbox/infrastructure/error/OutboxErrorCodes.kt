package com.restaurant.outbox.infrastructure.error

import com.restaurant.common.domain.error.ErrorCode

enum class OutboxErrorCodes(
    override val code: String,
    override val message: String,
) : ErrorCode {
    MESSAGE_NOT_FOUND("OUTBOX-001", "Outbox message not found"),
    MESSAGE_PROCESSING_FAILED("OUTBOX-002", "Failed to process outbox message"),
    KAFKA_SEND_FAILED("OUTBOX-003", "Failed to send message to Kafka"),
    MAX_RETRIES_EXCEEDED("OUTBOX-004", "Maximum retry attempts exceeded"),
    INVALID_MESSAGE_STATUS("OUTBOX-005", "Invalid message status transition"),
    DATABASE_ERROR("OUTBOX-006", "Database operation failed"), 
    SERIALIZATION_ERROR("OUTBOX-007", "Message serialization failed"),
    DATABASE_OPERATION_FAILED("OUTBOX-008", "Database operation failed"), 
    UNEXPECTED_INFRA_ERROR("OUTBOX-009", "An unexpected infrastructure error occurred"),
}
