package com.restaurant.independent.outbox.application.error

/**
 * Exception thrown when outbox message storage operations fail.
 * This is a module-specific exception that does not depend on any domain exceptions.
 */
class OutboxStorageException : RuntimeException {
    /**
     * Creates a new OutboxStorageException with the specified error message.
     *
     * @param message The error message
     */
    constructor(message: String) : super(message)

    /**
     * Creates a new OutboxStorageException with the specified error message and cause.
     *
     * @param message The error message
     * @param cause The underlying cause of the failure
     */
    constructor(message: String, cause: Throwable) : super(message, cause)
}
