package com.restaurant.shared.outbox.application.exception

import com.restaurant.shared.outbox.application.error.OutboxApplicationErrorCode
import com.restaurant.shared.outbox.application.error.OutboxErrorCode // 변경: common 의존성 제거
import com.restaurant.shared.outbox.application.exception.OutboxBaseApplicationException // 변경: common 의존성 제거

open class OutboxApplicationException(
    override val errorCode: OutboxErrorCode, // 변경: common 의존성 제거
    message: String,
    cause: Throwable? = null,
) : OutboxBaseApplicationException(message, cause) // 변경: common 의존성 제거

class OutboxKafkaSendException(
    message: String,
) : OutboxApplicationException(OutboxApplicationErrorCode.KAFKA_SEND_FAILED, message)

class OutboxEventProcessingException(
    message: String,
) : OutboxApplicationException(OutboxApplicationErrorCode.EVENT_PROCESSING_FAILED, message)

class OutboxMaxRetriesReached(
    message: String,
) : OutboxApplicationException(OutboxApplicationErrorCode.MAX_RETRIES_REACHED, message)
