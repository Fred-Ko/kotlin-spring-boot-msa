package com.restaurant.outbox.infrastructure.error

enum class OutboxErrorCodes(
    val code: String,
    val message: String,
) {
    MESSAGE_NOT_FOUND("OUTBOX-001", "Outbox message not found"),
    MESSAGE_PROCESSING_FAILED("OUTBOX-002", "Failed to process outbox message"),
    KAFKA_SEND_FAILED("OUTBOX-003", "Failed to send message to Kafka"),
    MAX_RETRIES_EXCEEDED("OUTBOX-004", "Maximum retry attempts exceeded"),
    INVALID_MESSAGE_STATUS("OUTBOX-005", "Invalid message status transition"),
    DATABASE_ERROR("OUTBOX-006", "Database operation failed"), // 이전 빌드에서 DATABASE_OPERATION_FAILED 로 참조하고 있었으므로 동일하게 유지
    SERIALIZATION_ERROR("OUTBOX-007", "Message serialization failed"),
    DATABASE_OPERATION_FAILED("OUTBOX-008", "Database operation failed"), // 중복된 것 같지만, Exception에서 이렇게 참조하고 있었음. 확인 필요. 우선 추가.
    UNEXPECTED_INFRA_ERROR("OUTBOX-009", "An unexpected infrastructure error occurred"),
}
