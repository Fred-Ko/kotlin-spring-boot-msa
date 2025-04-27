package com.restaurant.user.domain.aggregate

/**
 * Represents the status of a user account.
 */
enum class UserStatus {
    ACTIVE, // Normal, active state
    INACTIVE, // Temporarily inactive, e.g., not logged in for a long time
    WITHDRAWN, // User has withdrawn from the service
}
