package com.restaurant.independent.outbox.api.error

/**
 * Outbox 모듈에서 발생하는 예외의 베이스 클래스
 */
sealed class OutboxException(
    val errorCode: OutboxErrorCode,
    override val message: String = errorCode.message,
    cause: Throwable? = null,
) : RuntimeException(message, cause) {
    /**
     * 메시지 직렬화 관련 예외
     */
    class SerializationException(
        errorCode: OutboxErrorCode = OutboxErrorCode.MESSAGE_SERIALIZATION_FAILED,
        override val message: String = errorCode.message,
        cause: Throwable? = null,
    ) : OutboxException(errorCode, message, cause)

    /**
     * 메시지 역직렬화 관련 예외
     */
    class DeserializationException(
        errorCode: OutboxErrorCode = OutboxErrorCode.MESSAGE_DESERIALIZATION_FAILED,
        override val message: String = errorCode.message,
        cause: Throwable? = null,
    ) : OutboxException(errorCode, message, cause)

    /**
     * Kafka 전송 관련 예외
     */
    class KafkaProducerException(
        errorCode: OutboxErrorCode = OutboxErrorCode.KAFKA_PRODUCER_ERROR,
        override val message: String = errorCode.message,
        cause: Throwable? = null,
    ) : OutboxException(errorCode, message, cause)

    /**
     * 데이터베이스 작업 관련 예외
     */
    class DatabaseOperationException(
        errorCode: OutboxErrorCode = OutboxErrorCode.DATABASE_OPERATION_FAILED,
        override val message: String = errorCode.message,
        cause: Throwable? = null,
    ) : OutboxException(errorCode, message, cause)

    /**
     * 예상치 못한 인프라 예외
     */
    class UnexpectedInfraException(
        errorCode: OutboxErrorCode = OutboxErrorCode.UNEXPECTED_INFRA_ERROR,
        override val message: String = errorCode.message,
        cause: Throwable? = null,
    ) : OutboxException(errorCode, message, cause)

    /**
     * 메시지 폴링 관련 예외
     */
    class PollingException(
        override val message: String,
        cause: Throwable? = null,
    ) : OutboxException(OutboxErrorCode.UNEXPECTED_INFRA_ERROR, message, cause)

    /**
     * 메시지 전송 관련 예외
     */
    class MessageSendException(
        override val message: String,
        cause: Throwable? = null,
    ) : OutboxException(OutboxErrorCode.KAFKA_PRODUCER_ERROR, message, cause)

    /**
     * 최대 재시도 횟수 초과 예외
     */
    class MaxRetriesExceededException(
        override val message: String,
        cause: Throwable? = null,
    ) : OutboxException(OutboxErrorCode.UNEXPECTED_INFRA_ERROR, message, cause)

    /**
     * 직렬화 실패 예외 (간단 버전)
     */
    data class SerializationFailed(
        override val cause: Throwable?,
    ) : OutboxException(
            OutboxErrorCode.MESSAGE_SERIALIZATION_FAILED,
            "Failed to serialize outbox message payload.",
            cause,
        )

    /**
     * 역직렬화 실패 예외 (간단 버전)
     */
    data class DeserializationFailed(
        override val cause: Throwable?,
    ) : OutboxException(
            OutboxErrorCode.MESSAGE_DESERIALIZATION_FAILED,
            "Failed to deserialize outbox message payload.",
            cause,
        )

    /**
     * Kafka 전송 실패 예외 (간단 버전)
     */
    data class KafkaSendFailed(
        override val cause: Throwable?,
    ) : OutboxException(
            OutboxErrorCode.KAFKA_PRODUCER_ERROR,
            "Error occurred while sending message to Kafka.",
            cause,
        )

    /**
     * 데이터베이스 작업 실패 예외 (간단 버전)
     */
    data class DatabaseOperationFailed(
        override val cause: Throwable?,
    ) : OutboxException(
            OutboxErrorCode.DATABASE_OPERATION_FAILED,
            "Database operation failed for outbox message.",
            cause,
        )

    /**
     * 예상치 못한 인프라 오류 예외 (간단 버전)
     */
    data class UnexpectedInfrastructureError(
        override val cause: Throwable?,
    ) : OutboxException(
            OutboxErrorCode.UNEXPECTED_INFRA_ERROR,
            "An unexpected infrastructure error occurred.",
            cause,
        )
}
