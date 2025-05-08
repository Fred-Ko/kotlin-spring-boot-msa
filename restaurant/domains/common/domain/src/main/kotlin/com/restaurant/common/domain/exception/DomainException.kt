package com.restaurant.common.domain.exception

interface ErrorCode {
    val code: String
    val message: String
}

/**
 * Base class for all custom domain exceptions.
 * Requires subclasses to provide an ErrorCode. (Rule 68)
 */
open class DomainException(
    val errorCode: ErrorCode,
    override val message: String = errorCode.message,
    cause: Throwable? = null,
) : RuntimeException(message, cause)
