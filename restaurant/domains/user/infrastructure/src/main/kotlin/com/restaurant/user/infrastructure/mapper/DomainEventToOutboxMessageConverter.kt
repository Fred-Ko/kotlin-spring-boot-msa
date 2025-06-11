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
        headers["schemaVersion"] = "v${domainEvent.version}" // 이벤트에서 추출한 버전 사용
        headers["version"] = domainEvent.version.toString() // 버전 정보 추가

        return OutboxMessage(
            payload = payloadAsJsonString, // JSON 문자열로 직렬화된 payload 전달
            topic = topic,
            headers = headers,
            aggregateType = domainEvent.aggregateType,
            aggregateId = domainEvent.aggregateId,
            eventType = determineEventType(domainEvent),
        )
    }

    /**
     * 새로운 토픽 네이밍 정책: <환경>.<팀>.<도메인>.<데이터유형>.<액션>.<버전>
     * 각 이벤트 타입별로 개별 토픽 생성 (1:1 분리 정책)
     */
    private fun determineTopic(event: UserEvent): String {
        val environment = System.getenv("APP_ENV") ?: "dev"
        val team = System.getenv("TEAM_NAME") ?: "restaurant" // 팀 정보 환경변수에서 추출
        val domain = "user"
        val dataType = "event" // 데이터 유형
        val action = determineAction(event) // 이벤트별 액션 결정
        val version = "v${event.version}" // 이벤트에서 버전 추출

        return "$environment.$team.$domain.$dataType.$action.$version"
    }

    /**
     * 이벤트 타입별 액션 결정
     */
    private fun determineAction(event: UserEvent): String =
        when (event) {
            is UserEvent.Created -> "created"
            is UserEvent.Deleted -> "deleted"
            is UserEvent.PasswordChanged -> "password-changed"
            is UserEvent.ProfileUpdated -> "profile-updated"
            is UserEvent.AddressAdded -> "address-added"
            is UserEvent.AddressUpdated -> "address-updated"
            is UserEvent.AddressDeleted -> "address-deleted"
            is UserEvent.Withdrawn -> "withdrawn"
            is UserEvent.Deactivated -> "deactivated"
            is UserEvent.Activated -> "activated"
            else -> "unknown"
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
