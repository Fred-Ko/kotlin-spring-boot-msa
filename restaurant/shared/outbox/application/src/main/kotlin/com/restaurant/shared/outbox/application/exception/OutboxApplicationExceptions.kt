package com.restaurant.shared.outbox.application.exception

import com.restaurant.common.core.error.ErrorCode
import com.restaurant.common.core.exception.ApplicationException
import com.restaurant.shared.outbox.application.error.OutboxApplicationErrorCode

open class OutboxApplicationException(
    override val errorCode: ErrorCode,
    message: String,
) : ApplicationException(message)

class OutboxKafkaSendException(
    message: String,
) : OutboxApplicationException(OutboxApplicationErrorCode.KAFKA_SEND_FAILED, message)

class OutboxEventProcessingException(
    message: String,
) : OutboxApplicationException(OutboxApplicationErrorCode.EVENT_PROCESSING_FAILED, message)

class OutboxMaxRetriesReached(
    message: String,
) : OutboxApplicationException(OutboxApplicationErrorCode.MAX_RETRIES_REACHED, message)
