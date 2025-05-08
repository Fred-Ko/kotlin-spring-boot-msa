package com.restaurant.user.domain.event

import com.restaurant.common.domain.event.DomainEvent
import com.restaurant.user.domain.vo.UserId
import java.time.Instant
import java.util.UUID

/**
 * Sealed class grouping all domain events related to the User aggregate. (Rule 34)
 */
sealed class UserEvent(
    override val eventId: UUID = UUID.randomUUID(),
    override val occurredAt: Instant,
    open val userId: UserId,
) : DomainEvent {
    override val aggregateId: String
        get() = userId.value.toString()
    override val aggregateType: String
        get() = "User"

    /**
     * User Created Event
     */
    data class Created(
        val username: String,
        val email: String,
        val name: String,
        val phoneNumber: String?,
        val userType: String,
        override val userId: UserId,
        override val eventId: UUID = UUID.randomUUID(),
        override val occurredAt: Instant,
    ) : UserEvent(eventId, occurredAt, userId)

    /**
     * User Password Changed Event
     */
    data class PasswordChanged(
        override val userId: UserId,
        override val eventId: UUID = UUID.randomUUID(),
        override val occurredAt: Instant,
    ) : UserEvent(eventId, occurredAt, userId)

    /**
     * User Profile Updated Event
     */
    data class ProfileUpdated(
        val name: String,
        val phoneNumber: String?,
        override val userId: UserId,
        override val eventId: UUID = UUID.randomUUID(),
        override val occurredAt: Instant,
    ) : UserEvent(eventId, occurredAt, userId)

    /**
     * Represents address data within user events.
     */
    data class AddressData(
        val id: String,
        val name: String,
        val streetAddress: String,
        val city: String,
        val state: String,
        val country: String,
        val zipCode: String,
        val isDefault: Boolean,
    )

    /**
     * User Address Added Event
     */
    data class AddressRegistered(
        val address: AddressData,
        override val userId: UserId,
        override val eventId: UUID = UUID.randomUUID(),
        override val occurredAt: Instant,
    ) : UserEvent(eventId, occurredAt, userId)

    /**
     * User Address Updated Event
     */
    data class AddressUpdated(
        val address: AddressData,
        override val userId: UserId,
        override val eventId: UUID = UUID.randomUUID(),
        override val occurredAt: Instant,
    ) : UserEvent(eventId, occurredAt, userId)

    /**
     * User Address Deleted Event (Renamed from Removed)
     */
    data class AddressDeleted(
        val addressId: String,
        override val userId: UserId,
        override val eventId: UUID = UUID.randomUUID(),
        override val occurredAt: Instant,
    ) : UserEvent(eventId, occurredAt, userId)

    /**
     * User Withdrawn Event (Renamed from UserWithdrawn)
     */
    data class Withdrawn(
        override val userId: UserId,
        override val eventId: UUID = UUID.randomUUID(),
        override val occurredAt: Instant,
    ) : UserEvent(eventId, occurredAt, userId)
}
