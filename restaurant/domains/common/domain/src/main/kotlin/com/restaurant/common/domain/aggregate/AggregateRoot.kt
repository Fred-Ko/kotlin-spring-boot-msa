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
    open fun getDomainEvents(): List<DomainEvent> = domainEvents.toList()

    /**
     * Clears the list of recorded domain events.
     */
    open fun clearDomainEvents() {
        domainEvents.clear()
    }

    /**
     * Adds a domain event to the list of recorded events.
     * This method is protected to ensure it's only called within the Aggregate implementation.
     */
    protected fun addDomainEvent(event: DomainEvent) {
        domainEvents.add(event)
    }
}
