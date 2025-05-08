package com.restaurant.outbox.infrastructure.exception

import com.restaurant.outbox.infrastructure.error.OutboxErrorCodes

/**
 * Outbox 모듈에서 발생하는 예외의 베이스 클래스
 */
sealed class OutboxException(
    val errorCode: OutboxErrorCodes,
    message: String? = null,
    cause: Throwable? = null,
) : RuntimeException(message ?: errorCode.message, cause) {
    class MessageProcessingException(
        message: String? = null,
        cause: Throwable? = null,
    ) : OutboxException(OutboxErrorCodes.MESSAGE_PROCESSING_FAILED, message, cause)

    class SerializationException(
        message: String? = null,
        cause: Throwable? = null,
    ) : OutboxException(OutboxErrorCodes.MESSAGE_SERIALIZATION_FAILED, message, cause)

    class DeserializationException(
        message: String? = null,
        cause: Throwable? = null,
    ) : OutboxException(OutboxErrorCodes.MESSAGE_DESERIALIZATION_FAILED, message, cause)

    class KafkaSendException(
        message: String? = null,
        cause: Throwable? = null,
    ) : OutboxException(OutboxErrorCodes.KAFKA_SEND_FAILED, message, cause)

    class MaxRetriesExceededException(
        message: String? = null,
        cause: Throwable? = null,
    ) : OutboxException(OutboxErrorCodes.MAX_RETRIES_EXCEEDED, message, cause)

    /**
     * 데이터베이스 작업 관련 예외
     */
    class DatabaseOperationException(
        message: String? = null,
        cause: Throwable? = null,
    ) : OutboxException(OutboxErrorCodes.DATABASE_OPERATION_FAILED, message, cause)

    /**
     * 예상치 못한 인프라 예외
     */
    class UnexpectedInfrastructureException(
        message: String? = null,
        cause: Throwable? = null,
    ) : OutboxException(OutboxErrorCodes.UNEXPECTED_INFRA_ERROR, message, cause)

    // Removed duplicated simple data class exceptions (SerializationFailed, etc.)
}
