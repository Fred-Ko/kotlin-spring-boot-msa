package com.restaurant.independent.outbox.infrastructure.error // 패키지명 수정

import com.restaurant.common.core.error.ErrorCode
import com.restaurant.common.core.exception.InfrastructureException

/**
 * Outbox 인프라스트럭처 레이어 관련 예외
 */
sealed class OutboxException(
    // InfrastructureException은 ErrorCode를 직접 갖지 않으므로, 여기서 선언
    open val errorCode: ErrorCode,
    override val message: String,
    override val cause: Throwable? = null,
) : InfrastructureException(message) { // InfrastructureException 생성자에는 message만 전달

    data class SerializationFailed(
        override val cause: Throwable?,
    ) : OutboxException(
            OutboxErrorCode.MESSAGE_SERIALIZATION_FAILED,
            "Failed to serialize outbox message payload.",
            cause,
        )

    data class DeserializationFailed(
        override val cause: Throwable?,
    ) : OutboxException(
            OutboxErrorCode.MESSAGE_DESERIALIZATION_FAILED,
            "Failed to deserialize outbox message payload.",
            cause,
        )

    data class KafkaSendFailed(
        override val cause: Throwable?,
    ) : OutboxException(
            OutboxErrorCode.KAFKA_PRODUCER_ERROR,
            "Error occurred while sending message to Kafka.",
            cause,
        )

    data class DatabaseOperationFailed(
        override val cause: Throwable?,
    ) : OutboxException(
            OutboxErrorCode.DATABASE_OPERATION_FAILED,
            "Database operation failed for outbox message.",
            cause,
        )

    data class UnexpectedInfrastructureError(
        override val cause: Throwable?,
    ) : OutboxException(
            OutboxErrorCode.UNEXPECTED_INFRA_ERROR,
            "An unexpected infrastructure error occurred.",
            cause,
        )

    // 필요한 다른 Outbox 인프라 예외 추가
}
