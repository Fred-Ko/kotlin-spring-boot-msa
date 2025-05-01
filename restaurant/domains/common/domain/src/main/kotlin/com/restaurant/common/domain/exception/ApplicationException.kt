package com.restaurant.common.exception

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

    /**
     * Secondary constructor to initialize with an ErrorCode.
     * The message defaults to the errorCode's message.
     */
    constructor(
        errorCode: ErrorCode,
        message: String? = errorCode.message,
        cause: Throwable? = null,
    ) : this(message, cause) {
        // Note: abstract val 'errorCode' must be overridden in subclasses.
    }
}
