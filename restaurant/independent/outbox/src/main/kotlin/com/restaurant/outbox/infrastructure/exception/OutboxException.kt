package com.restaurant.outbox.infrastructure.exception

import com.restaurant.outbox.infrastructure.error.OutboxErrorCode

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

    // Removed duplicated simple data class exceptions (SerializationFailed, etc.)
}
