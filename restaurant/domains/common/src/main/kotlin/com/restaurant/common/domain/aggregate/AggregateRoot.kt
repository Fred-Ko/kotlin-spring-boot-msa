package com.restaurant.common.domain.aggregate

import com.restaurant.common.domain.event.DomainEvent

abstract class AggregateRoot {
    private val domainEvents: MutableList<DomainEvent> = mutableListOf()

    protected fun addDomainEvent(event: DomainEvent) {
        domainEvents.add(event)
    }

    fun getDomainEvents(): List<DomainEvent> = domainEvents.toList()

    fun clearDomainEvents() {
        domainEvents.clear()
    }
}
