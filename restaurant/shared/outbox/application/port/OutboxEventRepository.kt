package com.restaurant.shared.outbox.application.port

import com.restaurant.common.domain.event.DomainEvent

interface OutboxEventRepository {
    fun save(
        events: List<DomainEvent>,
        aggregateType: String,
        aggregateId: String,
    )
}
