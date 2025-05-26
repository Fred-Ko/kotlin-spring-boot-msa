package com.restaurant.common.domain.exception

import com.restaurant.common.domain.error.ErrorCode

/**
 * Base class for all custom domain exceptions.
 * Requires subclasses to provide an ErrorCode. (Rule 68)
 */
abstract class DomainException(
    message: String? = null,
    cause: Throwable? = null,
) : RuntimeException(message, cause) {
    abstract val errorCode: ErrorCode
}
