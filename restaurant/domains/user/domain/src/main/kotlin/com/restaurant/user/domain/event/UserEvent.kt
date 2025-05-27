package com.restaurant.user.domain.event

import com.restaurant.common.domain.event.DomainEvent
import com.restaurant.user.domain.vo.AddressId
import com.restaurant.user.domain.vo.UserId
import java.time.Instant
import java.util.UUID

/**
 * Sealed class grouping all domain events related to the User aggregate. (Rule 34)
 */

sealed class UserEvent : DomainEvent {
    abstract override val eventId: UUID

    abstract override val occurredAt: Instant

    abstract val id: UserId

    override val aggregateId: String
        get() = id.value.toString()
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
        override val id: UserId,
        override val eventId: UUID = UUID.randomUUID(),
        override val occurredAt: Instant,
    ) : UserEvent()

    /**
     * User Deleted Event
     */

    data class Deleted(
        override val id: UserId,
        override val eventId: UUID = UUID.randomUUID(),
        override val occurredAt: Instant,
    ) : UserEvent()

    /**
     * User Password Changed Event
     */

    data class PasswordChanged(
        override val id: UserId,
        override val eventId: UUID = UUID.randomUUID(),
        override val occurredAt: Instant,
    ) : UserEvent()

    /**
     * User Profile Updated Event
     */

    data class ProfileUpdated(
        val name: String,
        val phoneNumber: String?,
        override val id: UserId,
        override val eventId: UUID = UUID.randomUUID(),
        override val occurredAt: Instant,
    ) : UserEvent()

    /**
     * User Address Added Event
     */

    data class AddressAdded(
        val addressId: AddressId,
        override val id: UserId,
        override val eventId: UUID = UUID.randomUUID(),
        override val occurredAt: Instant,
    ) : UserEvent()

    /**
     * User Address Updated Event
     */

    data class AddressUpdated(
        val addressId: AddressId,
        val name: String,
        val streetAddress: String,
        val detailAddress: String?,
        val city: String,
        val state: String,
        val country: String,
        val zipCode: String,
        val isDefault: Boolean,
        override val id: UserId,
        override val eventId: UUID = UUID.randomUUID(),
        override val occurredAt: Instant,
    ) : UserEvent()

    /**
     * User Address Deleted Event
     */

    data class AddressDeleted(
        val addressId: AddressId,
        override val id: UserId,
        override val eventId: UUID = UUID.randomUUID(),
        override val occurredAt: Instant,
    ) : UserEvent()

    /**
     * User Withdrawn Event
     */

    data class Withdrawn(
        override val id: UserId,
        override val eventId: UUID = UUID.randomUUID(),
        override val occurredAt: Instant,
    ) : UserEvent()

    /**
     * User Deactivated Event
     */

    data class Deactivated(
        override val id: UserId,
        override val eventId: UUID = UUID.randomUUID(),
        override val occurredAt: Instant,
    ) : UserEvent()

    /**
     * User Activated Event
     */

    data class Activated(
        override val id: UserId,
        override val eventId: UUID = UUID.randomUUID(),
        override val occurredAt: Instant,
    ) : UserEvent()

    /**
     * Address Data for events (Rule 33, 34)
     */

    data class AddressData(
        val id: String,
        val name: String,
        val streetAddress: String,
        val detailAddress: String?,
        val city: String,
        val state: String,
        val country: String,
        val zipCode: String,
        val isDefault: Boolean,
    )
}
