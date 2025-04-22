package com.restaurant.independent.outbox.infrastructure.error // 패키지명 수정

import com.restaurant.common.core.error.ErrorCode

/**
 * Outbox 인프라스트럭처 레이어 관련 에러 코드 Enum
 */
enum class OutboxErrorCode(
    override val code: String,
    override val message: String,
) : ErrorCode {
    MESSAGE_SERIALIZATION_FAILED("OUTBOX-INFRA-001", "Failed to serialize outbox message payload."),
    MESSAGE_DESERIALIZATION_FAILED("OUTBOX-INFRA-002", "Failed to deserialize outbox message payload."),
    KAFKA_PRODUCER_ERROR("OUTBOX-INFRA-003", "Error occurred while sending message to Kafka."),
    DATABASE_OPERATION_FAILED("OUTBOX-INFRA-004", "Database operation failed for outbox message."),
    UNEXPECTED_INFRA_ERROR("OUTBOX-INFRA-999", "An unexpected infrastructure error occurred in the outbox module."),
    // 필요한 다른 Outbox 인프라 에러 코드 추가
}
