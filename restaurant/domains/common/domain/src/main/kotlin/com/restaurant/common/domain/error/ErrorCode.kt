package com.restaurant.common.domain.error

/**
 * Base interface for all error codes in the system.
 * Error codes should follow the format: {DOMAIN}-{LAYER}-{CODE}
 */
interface ErrorCode {
    /**
     * The unique code for this error.
     * Format: {DOMAIN}-{LAYER}-{CODE}
     * Example: USER-DOMAIN-001, USER-APP-002
     */
    val code: String

    /**
     * A human-readable message describing this error.
     */
    val message: String
}
