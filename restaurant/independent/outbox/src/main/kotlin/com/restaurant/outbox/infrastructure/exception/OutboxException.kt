package com.restaurant.outbox.infrastructure.exception

import com.restaurant.outbox.infrastructure.error.OutboxErrorCodes

/** Outbox 예외 베이스 클래스 */
sealed class OutboxException(
    val errorCode: OutboxErrorCodes,
    message: String? = null,
    cause: Throwable? = null,
) : RuntimeException(message ?: errorCode.message, cause) {
    class MessageNotFoundException(
        messageId: Long,
        cause: Throwable? = null,
    ) : OutboxException(
        OutboxErrorCodes.MESSAGE_NOT_FOUND,
        "Message not found with id: $messageId",
        cause,
    )

    class MessageProcessingFailedException(
        message: String? = null,
        cause: Throwable? = null,
    ) : OutboxException(
        OutboxErrorCodes.MESSAGE_PROCESSING_FAILED,
        message,
        cause,
    )

    class KafkaSendFailedException(
        message: String? = null,
        cause: Throwable? = null,
    ) : OutboxException(
        OutboxErrorCodes.KAFKA_SEND_FAILED,
        message,
        cause,
    )

    class MaxRetriesExceededException(
        messageId: Long,
        maxRetries: Int,
        cause: Throwable? = null,
    ) : OutboxException(
        OutboxErrorCodes.MAX_RETRIES_EXCEEDED,
        "Maximum retry attempts ($maxRetries) exceeded for message: $messageId",
        cause,
    )

    class InvalidMessageStatusException(
        currentStatus: String,
        newStatus: String,
        cause: Throwable? = null,
    ) : OutboxException(
        OutboxErrorCodes.INVALID_MESSAGE_STATUS,
        "Invalid status transition from $currentStatus to $newStatus",
        cause,
    )

    class DatabaseException(
        message: String? = null,
        cause: Throwable? = null,
    ) : OutboxException(
        OutboxErrorCodes.DATABASE_ERROR,
        message,
        cause,
    )

    class SerializationException(
        message: String? = null,
        cause: Throwable? = null,
    ) : OutboxException(
        OutboxErrorCodes.SERIALIZATION_ERROR,
        message,
        cause,
    )

    class DatabaseOperationException(
        message: String? = null,
        cause: Throwable? = null,
    ) : OutboxException(OutboxErrorCodes.DATABASE_OPERATION_FAILED, message, cause)


    class UnexpectedInfrastructureException(
        message: String? = null,
        cause: Throwable? = null,
    ) : OutboxException(OutboxErrorCodes.UNEXPECTED_INFRA_ERROR, message, cause)
}
