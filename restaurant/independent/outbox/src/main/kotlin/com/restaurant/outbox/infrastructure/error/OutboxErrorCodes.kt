package com.restaurant.outbox.infrastructure.error

enum class OutboxErrorCodes(
    val code: String,
    val message: String,
) {
    MESSAGE_PROCESSING_FAILED("OUTBOX-001", "Failed to process outbox message"),
    MESSAGE_SERIALIZATION_FAILED("OUTBOX-002", "Failed to serialize outbox message"),
    MESSAGE_DESERIALIZATION_FAILED("OUTBOX-003", "Failed to deserialize outbox message"),
    KAFKA_SEND_FAILED("OUTBOX-004", "Failed to send message to Kafka"),
    MAX_RETRIES_EXCEEDED("OUTBOX-005", "Maximum retry attempts exceeded for message"),
    DATABASE_OPERATION_FAILED("OUTBOX-006", "Database operation failed"),
    UNEXPECTED_INFRA_ERROR("OUTBOX-007", "Unexpected infrastructure error occurred"),
}
