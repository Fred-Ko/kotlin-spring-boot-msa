package com.restaurant.shared.outbox.infrastructure.persistence.extensions

import com.restaurant.shared.outbox.application.dto.OutboxEventPollingDto
import com.restaurant.shared.outbox.infrastructure.entity.OutboxEventEntity

fun OutboxEventEntity.toPollingDto(): OutboxEventPollingDto =
    OutboxEventPollingDto(
        id = this.id,
        eventId = this.eventId,
        aggregateType = this.aggregateType,
        aggregateId = this.aggregateId,
        eventType = this.eventType,
        payload = this.payload,
        status = this.status,
        retryCount = this.retryCount,
    )
