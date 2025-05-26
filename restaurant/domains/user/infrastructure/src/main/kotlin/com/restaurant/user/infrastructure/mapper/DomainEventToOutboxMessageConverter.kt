package com.restaurant.user.infrastructure.mapper

import com.github.avrokotlin.avro4k.Avro
import com.restaurant.common.domain.event.DomainEvent
import com.restaurant.outbox.application.dto.model.OutboxMessage
import com.restaurant.user.domain.event.UserEvent
import kotlinx.serialization.ExperimentalSerializationApi
import kotlinx.serialization.encodeToByteArray
import kotlinx.serialization.serializer
import org.springframework.stereotype.Component

@Component
class DomainEventToOutboxMessageConverter {
    @OptIn(ExperimentalSerializationApi::class)
    fun convert(domainEvent: DomainEvent): OutboxMessage {
        val avro = Avro { }
        val serializer = serializer(domainEvent::class.java)
        val payload = avro.encodeToByteArray(serializer, domainEvent)

        val topic = determineTopic(domainEvent)

        val headers = mutableMapOf<String, String>()
        headers["aggregateId"] = domainEvent.aggregateId
        headers["aggregateType"] = domainEvent.aggregateType
        headers["eventType"] = domainEvent::class.simpleName ?: "UnknownEvent"
        headers["eventId"] = domainEvent.eventId.toString()
        headers["occurredAt"] = domainEvent.occurredAt.toString()

        return OutboxMessage(
            payload = payload,
            topic = topic,
            headers = headers,
            aggregateType = domainEvent.aggregateType,
            aggregateId = domainEvent.aggregateId,
            eventType = domainEvent::class.simpleName ?: "UnknownEvent",
        )
    }

    private fun determineTopic(event: DomainEvent): String {
        val environment = System.getenv("APP_ENV") ?: "dev"
        var domain = "unknown"
        var entityName = "unknown"
        val eventTypeCategory = "domain-event"
        val version = "v1"

        when (event) {
            is UserEvent -> {
                domain = "user"
                entityName = "user"
            }
        }
        return "$environment.$domain.$eventTypeCategory.$entityName.$version"
    }
}
