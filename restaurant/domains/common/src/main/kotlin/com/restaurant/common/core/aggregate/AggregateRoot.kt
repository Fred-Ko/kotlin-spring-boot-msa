package com.restaurant.common.core.aggregate

import com.restaurant.common.core.domain.event.DomainEvent

/**
 * Base class for all Aggregate Roots.
 * Provides methods to manage domain events.
 * Rule 17, 18
 */
abstract class AggregateRoot {
    /**
     * Returns the list of recorded domain events.
     */
    abstract fun getDomainEvents(): List<DomainEvent>

    /**
     * Clears the list of recorded domain events.
     */
    abstract fun clearDomainEvents()

    // REMOVED: Internal addDomainEvent abstract or concrete method
    // Subclasses (like User) can implement their own internal helper (e.g., addDomainEventInternal)
    // or directly manage the event list (if mutable) ensuring proper cloning in copy().
}
