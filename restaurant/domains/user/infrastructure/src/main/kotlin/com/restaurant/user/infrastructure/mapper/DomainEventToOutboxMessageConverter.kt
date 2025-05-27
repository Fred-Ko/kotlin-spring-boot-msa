package com.restaurant.user.infrastructure.mapper

import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.databind.SerializationFeature
import com.fasterxml.jackson.databind.jsontype.DefaultBaseTypeLimitingValidator
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule
import com.fasterxml.jackson.module.kotlin.KotlinModule
import com.restaurant.common.domain.event.DomainEvent
import com.restaurant.outbox.application.dto.OutboxMessage
import com.restaurant.user.domain.event.UserEvent
import org.springframework.stereotype.Component

@Component
class DomainEventToOutboxMessageConverter {
    private val objectMapper: ObjectMapper =
        ObjectMapper()
            .registerModules(KotlinModule.Builder().build(), JavaTimeModule())
            .configure(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS, false)
            .apply {
                // Jackson 다형성 타입 처리를 위한 설정
                activateDefaultTyping(
                    DefaultBaseTypeLimitingValidator(),
                    ObjectMapper.DefaultTyping.NON_FINAL,
                )
            }

    fun convert(domainEvent: DomainEvent): OutboxMessage {
        // DomainEvent를 JSON으로 직렬화
        val payload = objectMapper.writeValueAsBytes(domainEvent)

        val topic = determineTopic(domainEvent)

        val headers = mutableMapOf<String, String>()
        headers["aggregateId"] = domainEvent.aggregateId
        headers["aggregateType"] = domainEvent.aggregateType
        headers["eventType"] = determineEventType(domainEvent)
        headers["eventId"] = domainEvent.eventId.toString()
        headers["occurredAt"] = domainEvent.occurredAt.toString()
        headers["contentType"] = "application/json"
        headers["schemaVersion"] = "v1"

        return OutboxMessage(
            payload = payload,
            topic = topic,
            headers = headers,
            aggregateType = domainEvent.aggregateType,
            aggregateId = domainEvent.aggregateId,
            eventType = determineEventType(domainEvent),
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

    private fun determineEventType(event: DomainEvent): String =
        when (event) {
            is UserEvent.Created -> "UserEvent.Created"
            is UserEvent.ProfileUpdated -> "UserEvent.ProfileUpdated"
            is UserEvent.PasswordChanged -> "UserEvent.PasswordChanged"
            is UserEvent.AddressAdded -> "UserEvent.AddressAdded"
            is UserEvent.AddressUpdated -> "UserEvent.AddressUpdated"
            is UserEvent.AddressDeleted -> "UserEvent.AddressDeleted"
            is UserEvent.Withdrawn -> "UserEvent.Withdrawn"
            is UserEvent.Deactivated -> "UserEvent.Deactivated"
            is UserEvent.Activated -> "UserEvent.Activated"
            is UserEvent.Deleted -> "UserEvent.Deleted"
            else -> event::class.simpleName ?: "UnknownEvent"
        }
}
