package com.restaurant.shared.outbox.infrastructure.exception

import com.restaurant.common.core.exception.InfrastructureException

/**
 * Base exception for Outbox infrastructure layer errors.
 */
open class OutboxInfrastructureException(
    message: String,
) : InfrastructureException(message)

class OutboxDatabaseException(
    message: String,
) : OutboxInfrastructureException(message)

class OutboxSerializationException(
    message: String,
    cause: Throwable? = null,
) : OutboxInfrastructureException(message)

class OutboxDeserializationException(
    message: String,
    cause: Throwable? = null,
) : OutboxInfrastructureException(message)
