package com.restaurant.common.domain.exception

import com.restaurant.common.domain.error.ErrorCode

/**
 * Base class for all custom domain exceptions.
 * Requires subclasses to provide an ErrorCode. (Rule 68)
 */
open class DomainException(
    val errorCode: ErrorCode,
    override val message: String = errorCode.message,
    cause: Throwable? = null,
) : RuntimeException(message, cause)
