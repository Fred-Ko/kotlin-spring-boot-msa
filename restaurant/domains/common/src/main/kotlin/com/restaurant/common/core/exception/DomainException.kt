package com.restaurant.common.core.exception

import com.restaurant.common.core.error.ErrorCode

/**
 * Base class for all custom domain exceptions.
 * Requires subclasses to provide an ErrorCode. (Rule 68)
 */
abstract class DomainException(
    message: String? = null,
    cause: Throwable? = null,
) : RuntimeException(message, cause) {
    /**
     * The error code associated with this domain exception.
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
        // It cannot be assigned here in the abstract class constructor.
    }
}
