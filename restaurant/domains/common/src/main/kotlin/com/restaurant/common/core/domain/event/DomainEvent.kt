package com.restaurant.common.core.domain.event

import java.time.Instant
import java.util.UUID

/**
 * Base interface for domain events. (Rule 32)
 */
interface DomainEvent {
    val eventId: UUID
        get() = UUID.randomUUID()

    val occurredAt: Instant
        get() = Instant.now()

    val aggregateId: String
    val aggregateType: String
}
