package com.restaurant.shared.outbox.infrastructure.exception

// import com.restaurant.common.core.exception.InfrastructureException // 변경: common 의존성 제거

/**
 * Base exception for Outbox infrastructure layer errors.
 */
open class OutboxInfrastructureException(
    message: String,
    cause: Throwable? = null,
) : OutboxBaseInfrastructureException(message, cause) // 변경: common 의존성 제거

class OutboxDatabaseException(
    message: String,
    cause: Throwable? = null,
) : OutboxBaseInfrastructureException(message, cause) // 변경: common 의존성 제거

class OutboxSerializationException(
    message: String,
    cause: Throwable? = null,
) : OutboxBaseInfrastructureException(message, cause) // 변경: common 의존성 제거

class OutboxDeserializationException(
    message: String,
    cause: Throwable? = null,
) : OutboxBaseInfrastructureException(message, cause) // 변경: common 의존성 제거
