package com.restaurant.domain.user.event

import com.restaurant.common.domain.event.DomainEvent
import com.restaurant.domain.user.aggregate.User
import com.restaurant.domain.user.vo.AddressId
import com.restaurant.domain.user.vo.UserId
import java.time.LocalDateTime

sealed class UserEvent(
    open val userId: UserId,
    override val eventId: String,
    override val occurredAt: LocalDateTime,
) : DomainEvent {
    override val aggregateId: String
        get() = userId.value.toString()
    override val aggregateType: String
        get() = User::class.java.simpleName

    data class Created(
        override val userId: UserId,
        val email: String,
        val name: String,
        override val eventId: String,
        override val occurredAt: LocalDateTime,
    ) : UserEvent(userId, eventId, occurredAt)

    data class ProfileUpdated(
        override val userId: UserId,
        val name: String,
        override val eventId: String,
        override val occurredAt: LocalDateTime,
    ) : UserEvent(userId, eventId, occurredAt)

    data class PasswordChanged(
        override val userId: UserId,
        override val eventId: String,
        override val occurredAt: LocalDateTime,
    ) : UserEvent(userId, eventId, occurredAt)

    data class AddressAdded(
        override val userId: UserId,
        val addressId: AddressId,
        override val eventId: String,
        override val occurredAt: LocalDateTime,
    ) : UserEvent(userId, eventId, occurredAt)

    data class AddressUpdated(
        override val userId: UserId,
        val addressId: AddressId,
        override val eventId: String,
        override val occurredAt: LocalDateTime,
    ) : UserEvent(userId, eventId, occurredAt)

    data class AddressRemoved(
        override val userId: UserId,
        val addressId: AddressId,
        override val eventId: String,
        override val occurredAt: LocalDateTime,
    ) : UserEvent(userId, eventId, occurredAt)
}
