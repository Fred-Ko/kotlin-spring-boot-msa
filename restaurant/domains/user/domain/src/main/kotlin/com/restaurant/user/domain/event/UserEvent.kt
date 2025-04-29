package com.restaurant.user.domain.event

import com.restaurant.common.domain.event.DomainEvent
import com.restaurant.user.domain.vo.UserId
import java.time.Instant

/**
 * Sealed class grouping all domain events related to the User aggregate. (Rule 34)
 */
sealed class UserEvent(open val userId: UserId) : DomainEvent {
    override val aggregateId: String get() = userId.value.toString()
    override val aggregateType: String = "User"
    override val occurredAt: Instant = Instant.now()

    /**
     * User Created Event
     */
    data class Created(
        override val userId: UserId,
        val username: String,
        val email: String,
        val name: String,
        val phoneNumber: String?,
        val userType: String,
        val registeredAt: Instant,
    ) : UserEvent(userId) {
        override val occurredAt: Instant get() = registeredAt // Use registration time
    }

    /**
     * User Password Changed Event
     */
    data class PasswordChanged(
        override val userId: UserId,
        val changedAt: Instant,
    ) : UserEvent(userId) {
        override val occurredAt: Instant get() = changedAt
    }

    /**
     * User Profile Updated Event
     */
    data class ProfileUpdated(
        override val userId: UserId,
        val name: String,
        val phoneNumber: String?,
        val updatedAt: Instant,
    ) : UserEvent(userId) {
        override val occurredAt: Instant get() = updatedAt
    }

    /**
     * Represents address data within user events.
     */
    data class AddressData(
        val addressId: String,
        val street: String,
        val detail: String,
        val zipCode: String,
        val isDefault: Boolean,
    )

    /**
     * User Address Added Event
     */
    data class AddressAdded(
        override val userId: UserId,
        val address: AddressData,
        val addedAt: Instant,
    ) : UserEvent(userId) {
        override val occurredAt: Instant get() = addedAt
    }

    /**
     * User Address Updated Event
     */
    data class AddressUpdated(
        override val userId: UserId,
        val address: AddressData,
        val updatedAt: Instant,
    ) : UserEvent(userId) {
        override val occurredAt: Instant get() = updatedAt
    }

    /**
     * User Address Deleted Event (Renamed from Removed)
     */
    data class AddressDeleted(
        override val userId: UserId,
        val addressId: String,
        val deletedAt: Instant,
    ) : UserEvent(userId) {
        override val occurredAt: Instant get() = deletedAt
    }

    /**
     * User Withdrawn Event (Renamed from UserWithdrawn)
     */
    data class Withdrawn(
        override val userId: UserId,
        val withdrawnAt: Instant,
    ) : UserEvent(userId) {
        override val occurredAt: Instant get() = withdrawnAt
    }
}
