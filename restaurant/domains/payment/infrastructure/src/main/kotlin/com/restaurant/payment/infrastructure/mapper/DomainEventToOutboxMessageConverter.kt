package com.restaurant.payment.infrastructure.mapper

import com.restaurant.outbox.application.dto.OutboxMessage
import com.restaurant.payment.domain.event.PaymentEvent
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json
import org.springframework.stereotype.Component

/**
 * Payment Domain Event를 OutboxMessage로 변환하는 컴포넌트 (Rule 85)
 * Kotlinx Serialization을 사용하여 JSON 직렬화를 수행합니다.
 */
@Component
class DomainEventToOutboxMessageConverter(
    private val kotlinJson: Json, // 주입받은 Json Bean 사용
) {
    fun convert(domainEvent: PaymentEvent): OutboxMessage {
        // PaymentEvent 객체를 JSON 문자열로 직렬화 (주입받은 kotlinJson 사용)
        val payloadAsJsonString = kotlinJson.encodeToString(domainEvent)
        val topic = determineTopic(domainEvent)

        val headers = mutableMapOf<String, String>()
        headers["aggregateId"] = domainEvent.aggregateId
        headers["aggregateType"] = domainEvent.aggregateType
        headers["eventType"] = determineEventType(domainEvent)
        headers["eventId"] = domainEvent.eventId.toString()
        headers["occurredAt"] = domainEvent.occurredAt.toString()
        headers["contentType"] = "application/json"
        headers["schemaVersion"] = "v${domainEvent.version}"
        headers["version"] = domainEvent.version.toString()

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
     * 각 이벤트 타입별로 개별 토픽 생성 (1:1 분리 정책) (Rule VII.2.6, VII.2.20)
     */
    private fun determineTopic(event: PaymentEvent): String {
        val environment = System.getenv("APP_ENV") ?: "dev"
        val team = System.getenv("TEAM_NAME") ?: "payment-team" // Payment 팀 정보
        val domain = "payment"
        val dataType = "event" // 데이터 유형
        val action = determineAction(event) // 이벤트별 액션 결정
        val version = "v${event.version}" // 이벤트에서 버전 추출

        return "$environment.$team.$domain.$dataType.$action.$version"
    }

    /**
     * 이벤트 타입별 액션 결정
     */
    private fun determineAction(event: PaymentEvent): String =
        when (event) {
            is PaymentEvent.PaymentRequested -> "payment-requested"
            is PaymentEvent.PaymentApproved -> "payment-approved"
            is PaymentEvent.PaymentFailed -> "payment-failed"
            is PaymentEvent.PaymentRefunded -> "payment-refunded"
            is PaymentEvent.PaymentRefundFailed -> "payment-refund-failed"
            is PaymentEvent.PaymentMethodRegistered -> "payment-method-registered"
            else -> "unknown"
        }

    private fun determineEventType(event: PaymentEvent): String =
        when (event) {
            is PaymentEvent.PaymentRequested -> "PaymentEvent.PaymentRequested"
            is PaymentEvent.PaymentApproved -> "PaymentEvent.PaymentApproved"
            is PaymentEvent.PaymentFailed -> "PaymentEvent.PaymentFailed"
            is PaymentEvent.PaymentRefunded -> "PaymentEvent.PaymentRefunded"
            is PaymentEvent.PaymentRefundFailed -> "PaymentEvent.PaymentRefundFailed"
            is PaymentEvent.PaymentMethodRegistered -> "PaymentEvent.PaymentMethodRegistered"
            else -> event::class.simpleName ?: "UnknownEvent"
        }
}
