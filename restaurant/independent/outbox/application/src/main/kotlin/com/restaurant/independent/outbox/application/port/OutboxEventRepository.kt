package com.restaurant.independent.outbox.application.port

import com.restaurant.independent.outbox.application.event.OutboxDomainEvent

interface OutboxEventRepository {
    fun save(
        events: List<OutboxDomainEvent>,
        aggregateType: String,
        aggregateId: String,
    )
}
