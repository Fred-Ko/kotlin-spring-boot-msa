package com.restaurant.shared.outbox.application.error

import com.restaurant.common.core.error.ErrorCode
import org.springframework.http.HttpStatus

enum class OutboxApplicationErrorCode(
    override val code: String,
    override val message: String,
    override val status: HttpStatus,
) : ErrorCode {
    KAFKA_SEND_FAILED(
        "OUTBOX-APP-001",
        "Failed to send event to Kafka.",
        HttpStatus.INTERNAL_SERVER_ERROR,
    ),
    EVENT_PROCESSING_FAILED(
        "OUTBOX-APP-002",
        "Generic error during outbox event processing.",
        HttpStatus.INTERNAL_SERVER_ERROR,
    ),
    MAX_RETRIES_REACHED(
        "OUTBOX-APP-003",
        "Event processing failed after maximum retries.",
        HttpStatus.INTERNAL_SERVER_ERROR,
    ),
}
