package com.restaurant.user.domain.event

import com.restaurant.common.domain.event.DomainEvent
import com.restaurant.user.domain.vo.UserId
import kotlinx.serialization.Contextual
import kotlinx.serialization.Serializable
import java.time.Instant
import java.util.UUID

/**
 * Sealed class grouping all domain events related to the User aggregate. (Rule 34)
 */
@Serializable
sealed class UserEvent : DomainEvent {
    @Contextual abstract override val eventId: UUID

    @Contextual abstract override val occurredAt: Instant

    @Contextual abstract val userId: UserId

    override val aggregateId: String
        get() = userId.value.toString()
    override val aggregateType: String
        get() = "User"

    /**
     * User Created Event
     */
    @Serializable
    data class Created(
        val username: String,
        val email: String,
        val name: String,
        val phoneNumber: String?,
        val userType: String,
        @Contextual override val userId: UserId,
        @Contextual override val eventId: UUID = UUID.randomUUID(),
        @Contextual override val occurredAt: Instant,
    ) : UserEvent()

    /**
     * User Updated Event
     */
    @Serializable
    data class Updated(
        val username: String?,
        val email: String?,
        val name: String?,
        val phoneNumber: String?,
        val userType: String?,
        @Contextual override val userId: UserId,
        @Contextual override val eventId: UUID = UUID.randomUUID(),
        @Contextual override val occurredAt: Instant,
    ) : UserEvent()

    /**
     * User Deleted Event
     */
    @Serializable
    data class Deleted(
        @Contextual override val userId: UserId,
        @Contextual override val eventId: UUID = UUID.randomUUID(),
        @Contextual override val occurredAt: Instant,
    ) : UserEvent()

    /**
     * User Password Changed Event
     */
    @Serializable
    data class PasswordChanged(
        @Contextual override val userId: UserId,
        @Contextual override val eventId: UUID = UUID.randomUUID(),
        @Contextual override val occurredAt: Instant,
    ) : UserEvent()

    /**
     * User Profile Updated Event
     */
    @Serializable
    data class ProfileUpdated(
        val name: String,
        val phoneNumber: String?,
        @Contextual override val userId: UserId,
        @Contextual override val eventId: UUID = UUID.randomUUID(),
        @Contextual override val occurredAt: Instant,
    ) : UserEvent()

    /**
     * Represents address data within user events.
     */
    @Serializable
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

    /**
     * User Address Added Event
     */
    @Serializable
    data class AddressRegistered(
        val address: AddressData,
        @Contextual override val userId: UserId,
        @Contextual override val eventId: UUID = UUID.randomUUID(),
        @Contextual override val occurredAt: Instant,
    ) : UserEvent()

    /**
     * User Address Updated Event
     */
    @Serializable
    data class AddressUpdated(
        val address: AddressData,
        @Contextual override val userId: UserId,
        @Contextual override val eventId: UUID = UUID.randomUUID(),
        @Contextual override val occurredAt: Instant,
    ) : UserEvent()

    /**
     * User Address Deleted Event (Renamed from Removed)
     */
    @Serializable
    data class AddressDeleted(
        val addressId: String,
        @Contextual override val userId: UserId,
        @Contextual override val eventId: UUID = UUID.randomUUID(),
        @Contextual override val occurredAt: Instant,
    ) : UserEvent()

    /**
     * User Withdrawn Event (Renamed from UserWithdrawn)
     */
    @Serializable
    data class Withdrawn(
        @Contextual override val userId: UserId,
        @Contextual override val eventId: UUID = UUID.randomUUID(),
        @Contextual override val occurredAt: Instant,
    ) : UserEvent()
}
