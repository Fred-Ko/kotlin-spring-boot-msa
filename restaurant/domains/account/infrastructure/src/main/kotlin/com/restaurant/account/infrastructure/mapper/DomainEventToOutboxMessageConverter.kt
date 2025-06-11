package com.restaurant.account.infrastructure.mapper

import com.restaurant.account.domain.event.AccountEvent
import com.restaurant.common.domain.event.DomainEvent
import com.restaurant.outbox.application.dto.OutboxMessage
import kotlinx.serialization.Serializable
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json
import org.springframework.stereotype.Component

@Component
class DomainEventToOutboxMessageConverter(
    private val json: Json,
) {
    fun convertToOutboxMessages(events: List<DomainEvent>): List<OutboxMessage> =
        events.mapNotNull { event ->
            when (event) {
                is AccountEvent -> convertAccountEvent(event)
                else -> null
            }
        }

    @Serializable
    private data class EventWrapper(
        val event: AccountEvent,
        val eventType: String,
    )

    private fun convertAccountEvent(event: AccountEvent): OutboxMessage {
        val eventWrapper = EventWrapper(event, event::class.simpleName ?: "Unknown")
        val payload = json.encodeToString(eventWrapper)

        val headers =
            mapOf(
                "aggregateType" to event.aggregateType,
                "aggregateId" to event.aggregateId,
                "eventType" to (event::class.simpleName ?: "Unknown"),
                "contentType" to "application/json",
            )

        val topic = "dev.restaurant.account.event.${(event::class.simpleName ?: "unknown").lowercase()}"

        return OutboxMessage(
            aggregateType = event.aggregateType,
            aggregateId = event.aggregateId,
            eventType = event::class.simpleName ?: "Unknown",
            payload = payload,
            headers = headers,
            topic = topic,
        )
    }
}
