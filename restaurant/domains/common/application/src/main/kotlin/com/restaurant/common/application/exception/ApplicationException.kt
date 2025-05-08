package com.restaurant.common.application.exception

import com.restaurant.common.domain.error.ErrorCode

/**
 * Base class for all custom application exceptions.
 * Represents errors occurring in the application layer (e.g., use case execution failures).
 * Requires subclasses to provide an ErrorCode. (Rule 68)
 */
abstract class ApplicationException(
    message: String? = null,
    cause: Throwable? = null,
) : RuntimeException(message, cause) {
    /**
     * The error code associated with this application exception.
     */
    abstract val errorCode: ErrorCode
}
