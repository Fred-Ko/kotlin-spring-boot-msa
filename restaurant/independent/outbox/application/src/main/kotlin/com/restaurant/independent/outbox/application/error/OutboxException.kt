package com.restaurant.independent.outbox.application.error

/**
 * Outbox 모듈의 자체적인 예외 클래스 정의
 * Rule 67, 68, 80에 따라 common 모듈의 예외와 독립적으로 정의
 */
sealed class OutboxException(
    message: String,
    cause: Throwable? = null,
    val errorCode: OutboxErrorCode,
) : RuntimeException(message, cause) {
    /**
     * 메시지 저장 실패 예외
     */
    class MessageSaveException(
        message: String,
        cause: Throwable? = null,
    ) : OutboxException(
            message = message,
            cause = cause,
            errorCode = OutboxErrorCode.MESSAGE_SAVE_FAILED,
        )

    /**
     * 메시지 직렬화 실패 예외
     */
    class MessageSerializationException(
        message: String,
        cause: Throwable? = null,
    ) : OutboxException(
            message = message,
            cause = cause,
            errorCode = OutboxErrorCode.MESSAGE_SERIALIZATION_FAILED,
        )

    /**
     * 메시지 전송 실패 예외
     */
    class MessageSendException(
        message: String,
        cause: Throwable? = null,
    ) : OutboxException(
            message = message,
            cause = cause,
            errorCode = OutboxErrorCode.MESSAGE_SEND_FAILED,
        )

    /**
     * 최대 재시도 횟수 초과 예외
     */
    class MaxRetriesExceededException(
        message: String,
        cause: Throwable? = null,
    ) : OutboxException(
            message = message,
            cause = cause,
            errorCode = OutboxErrorCode.MAX_RETRIES_EXCEEDED,
        )

    /**
     * 메시지 폴링 실패 예외
     */
    class PollingException(
        message: String,
        cause: Throwable? = null,
    ) : OutboxException(
            message = message,
            cause = cause,
            errorCode = OutboxErrorCode.POLLING_ERROR,
        )

    /**
     * 예상치 못한 시스템 오류 예외
     */
    class UnexpectedErrorException(
        message: String,
        cause: Throwable? = null,
    ) : OutboxException(
            message = message,
            cause = cause,
            errorCode = OutboxErrorCode.UNEXPECTED_ERROR,
        )
}
