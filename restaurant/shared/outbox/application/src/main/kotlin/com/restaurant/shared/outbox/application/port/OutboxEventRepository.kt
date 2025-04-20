package com.restaurant.shared.outbox.application.port

import com.restaurant.common.domain.event.DomainEvent

/**
 * Port interface for saving domain events to the outbox.
 * This interface is technology-agnostic and resides in the application layer.
 * Other modules' infrastructure layers (e.g., UserRepositoryImpl) will depend on this port.
 */
interface OutboxEventRepository {
    /**
     * Saves a list of domain events associated with a specific aggregate.
     *
     * @param events The list of DomainEvent objects to save.
     * @param aggregateType The type of the aggregate that generated the events (e.g., "User").
     * @param aggregateId The domain ID (usually UUID) of the aggregate as a String.
     */
    fun save(
        events: List<DomainEvent>,
        aggregateType: String,
        aggregateId: String,
    )

    /**
     * Saves a single domain event associated with a specific aggregate.
     * Convenience method for saving a single event.
     *
     * @param event The DomainEvent object to save.
     * @param aggregateType The type of the aggregate that generated the event (e.g., "User").
     * @param aggregateId The domain ID (usually UUID) of the aggregate as a String.
     */
    fun save( // Overload for single event convenience
        event: DomainEvent,
        aggregateType: String,
        aggregateId: String,
    ) = save(listOf(event), aggregateType, aggregateId)
}
