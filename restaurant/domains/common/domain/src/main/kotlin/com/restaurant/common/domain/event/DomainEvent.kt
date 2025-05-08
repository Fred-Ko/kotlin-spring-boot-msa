package com.restaurant.common.domain.event

import java.time.Instant
import java.util.UUID

/**
 * Base interface for domain events. (Rule 32)
 */
interface DomainEvent {
    val eventId: UUID
    val occurredAt: Instant
    val aggregateId: String
    val aggregateType: String
}
