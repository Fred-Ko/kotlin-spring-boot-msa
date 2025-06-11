package com.restaurant.account.domain.event

import com.restaurant.account.domain.vo.AccountId
import com.restaurant.account.domain.vo.UserId
import com.restaurant.common.domain.event.DomainEvent
import kotlinx.serialization.Contextual
import kotlinx.serialization.Serializable
import java.time.Instant
import java.util.UUID

@Serializable
sealed class AccountEvent : DomainEvent {
    override val aggregateType: String = "Account"
    override val version: Int = 1
    abstract val accountId: AccountId
    override val aggregateId: String get() = accountId.value.toString()

    @Serializable
    data class AccountOpened(
        override val accountId: AccountId,
        val userId: UserId,
        @Contextual override val eventId: UUID = UUID.randomUUID(),
        @Contextual override val occurredAt: Instant = Instant.now(),
    ) : AccountEvent()

    @Serializable
    data class Deposited(
        override val accountId: AccountId,
        val amount: Long,
        @Contextual override val eventId: UUID = UUID.randomUUID(),
        @Contextual override val occurredAt: Instant = Instant.now(),
    ) : AccountEvent()

    @Serializable
    data class Withdrawn(
        override val accountId: AccountId,
        val amount: Long,
        @Contextual override val eventId: UUID = UUID.randomUUID(),
        @Contextual override val occurredAt: Instant = Instant.now(),
    ) : AccountEvent()

    @Serializable
    data class AccountClosed(
        override val accountId: AccountId,
        @Contextual override val eventId: UUID = UUID.randomUUID(),
        @Contextual override val occurredAt: Instant = Instant.now(),
    ) : AccountEvent()

    @Serializable
    data class AccountDeactivated(
        override val accountId: AccountId,
        @Contextual override val eventId: UUID = UUID.randomUUID(),
        @Contextual override val occurredAt: Instant = Instant.now(),
    ) : AccountEvent()
}
