package com.ddd.libs.outbox.publisher

import com.ddd.libs.outbox.model.OutboxEvent

interface OutboxEventPublisher {
    fun publish(event: OutboxEvent)
}
