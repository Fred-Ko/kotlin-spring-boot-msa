package com.ddd.support.model

import DomainEvent

abstract class DomainAggregateRoot {
    private val domainEvents = mutableListOf<DomainEvent>()

    fun registerEvent(event: DomainEvent) {
        domainEvents.add(event)
    }

    fun getDomainEvents(): List<DomainEvent> = domainEvents.toList()

    fun clearDomainEvents() {
        domainEvents.clear()
    }
}