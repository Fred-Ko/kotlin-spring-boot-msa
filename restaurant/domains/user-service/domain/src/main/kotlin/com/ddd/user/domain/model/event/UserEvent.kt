package com.ddd.user.domain.model.event

import DomainEvent
import java.time.Instant
import java.util.UUID

data class UserCreatedEventV1(
        override val eventId: UUID = UUID.randomUUID(),
        override val aggregateId: UUID,
        override val timestamp: Instant = Instant.now(),
        override val metadata: Map<String, String> = emptyMap(),
        val name: String,
        val email: String,
        val phoneNumber: String,
        val address: String,
        val status: String
) : DomainEvent {
        override val version: Long = 1
        override val eventType: String = "UserCreatedEventV1"
        override val payload: Any =
                mapOf(
                        "name" to name,
                        "email" to email,
                        "phoneNumber" to phoneNumber,
                        "address" to address
                )
}

data class UserUpdatedEventV1(
        override val eventId: UUID = UUID.randomUUID(),
        override val aggregateId: UUID,
        override val timestamp: Instant = Instant.now(),
        override val metadata: Map<String, String> = emptyMap(),
        val name: String?,
        val email: String?,
        val phoneNumber: String?,
        val address: String?,
        val status: String?
) : DomainEvent {
        override val version: Long = 1
        override val eventType: String = "UserUpdatedEventV1"
        override val payload: Any =
                mapOf(
                                "name" to name,
                                "email" to email,
                                "phoneNumber" to phoneNumber,
                                "address" to address,
                                "status" to status
                        )
                        .filterValues { it != null }
}
