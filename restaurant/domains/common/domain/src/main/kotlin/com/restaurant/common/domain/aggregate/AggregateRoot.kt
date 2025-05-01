package com.restaurant.common.domain.aggregate

import com.restaurant.common.domain.event.DomainEvent

/**
 * Base class for all Aggregate Roots.
 * Provides methods to manage domain events.
 * Rule 17, 18
 */
abstract class AggregateRoot {
    private val domainEvents: MutableList<DomainEvent> = mutableListOf()

    /**
     * Returns the list of recorded domain events.
     */
    fun getDomainEvents(): List<DomainEvent> = domainEvents.toList()

    /**
     * Clears the list of recorded domain events.
     */
    fun clearDomainEvents() {
        domainEvents.clear()
    }

    /**
     * Adds a domain event to the list of recorded events.
     * This method is internal to ensure it's only called within the Aggregate implementation.
     */
    internal fun addDomainEvent(event: DomainEvent) {
        domainEvents.add(event)
    }
}
