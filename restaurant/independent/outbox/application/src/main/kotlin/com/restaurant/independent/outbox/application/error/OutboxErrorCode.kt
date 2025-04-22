package com.restaurant.independent.outbox.application.error

/**
 * Outbox 모듈의 자체적인 에러 코드 정의
 * Rule 67, 80에 따라 common 모듈의 ErrorCode와 독립적으로 정의
 */
enum class OutboxErrorCode(
    val code: String,
    val message: String,
) {
    // Message 저장 관련 오류
    MESSAGE_SAVE_FAILED(
        code = "OUTBOX-001",
        message = "Failed to save outbox message",
    ),
    MESSAGE_SERIALIZATION_FAILED(
        code = "OUTBOX-002",
        message = "Failed to serialize outbox message",
    ),

    // Message 전송 관련 오류
    MESSAGE_SEND_FAILED(
        code = "OUTBOX-003",
        message = "Failed to send outbox message",
    ),
    MAX_RETRIES_EXCEEDED(
        code = "OUTBOX-004",
        message = "Maximum retry attempts exceeded for outbox message",
    ),

    // Polling 관련 오류
    POLLING_ERROR(
        code = "OUTBOX-005",
        message = "Error occurred during message polling",
    ),

    // 기타 시스템 오류
    UNEXPECTED_ERROR(
        code = "OUTBOX-999",
        message = "Unexpected error occurred in outbox processing",
    ),
}
