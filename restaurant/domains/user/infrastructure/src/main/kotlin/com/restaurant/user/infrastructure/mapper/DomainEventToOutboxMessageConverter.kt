package com.restaurant.user.infrastructure.mapper

// import com.fasterxml.jackson.databind.ObjectMapper // ObjectMapper import 제거
import com.restaurant.outbox.application.dto.OutboxMessage
import com.restaurant.user.domain.event.UserEvent
import kotlinx.serialization.encodeToString // kotlinx.serialization import 추가
import kotlinx.serialization.json.Json // kotlinx.serialization import 추가
import org.springframework.stereotype.Component

@Component
class DomainEventToOutboxMessageConverter(
    private val kotlinJson: Json, // 주입받은 Json Bean 사용
) {
    fun convert(domainEvent: UserEvent): OutboxMessage {
        // UserEvent 객체를 JSON 문자열로 직렬화 (주입받은 kotlinJson 사용)
        val payloadAsJsonString = kotlinJson.encodeToString(domainEvent)
        val topic = determineTopic(domainEvent)

        val headers = mutableMapOf<String, String>()
        headers["aggregateId"] = domainEvent.aggregateId
        headers["aggregateType"] = domainEvent.aggregateType
        headers["eventType"] = determineEventType(domainEvent)
        headers["eventId"] = domainEvent.eventId.toString()
        headers["occurredAt"] = domainEvent.occurredAt.toString()
        headers["contentType"] = "application/json" // KafkaJsonSchemaSerializer가 처리하므로 유지
        headers["schemaVersion"] = "v1" // 필요시 스키마 버전 명시

        return OutboxMessage(
            payload = payloadAsJsonString, // JSON 문자열로 직렬화된 payload 전달
            topic = topic,
            headers = headers,
            aggregateType = domainEvent.aggregateType,
            aggregateId = domainEvent.aggregateId,
            eventType = determineEventType(domainEvent),
        )
    }

    private fun determineTopic(event: UserEvent): String {
        val environment = System.getenv("APP_ENV") ?: "dev"
        val domain = "user"
        val entityName = "user"
        val eventTypeCategory = "events"
        val version = "v1"

        return "$environment.$domain.$eventTypeCategory.$entityName.$version"
    }

    private fun determineEventType(event: UserEvent): String =
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
