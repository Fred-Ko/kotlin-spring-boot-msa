package com.restaurant.payment.infrastructure.messaging

import kotlinx.serialization.json.Json
import org.slf4j.LoggerFactory
import org.springframework.kafka.annotation.KafkaListener
import org.springframework.stereotype.Component

/**
 * Payment 도메인 이벤트 소비자 (Rule VII.2.24)
 * 시나리오에 따라 OrderCreatedEvent, OrderCanceledEvent를 구독합니다.
 */
@Component
class PaymentEventConsumer(
    private val kotlinJson: Json,
) {
    private val log = LoggerFactory.getLogger(PaymentEventConsumer::class.java)

    /**
     * Order 생성 이벤트 처리 - 결제 프로세스 시작
     */
    @KafkaListener(
        topics = ["dev.order-team.order.event.order-created.v1"],
        groupId = "payment-service-group",
    )
    fun handleOrderCreatedEvent(jsonPayload: String) {
        try {
            log.info("Received OrderCreatedEvent: {}", jsonPayload)

            // TODO: OrderCreatedEvent를 JSON에서 역직렬화
            // val orderCreatedEvent = kotlinJson.decodeFromString<OrderCreatedEvent>(jsonPayload)

            // TODO: 결제 프로세스 시작 로직 구현
            // - Order 정보 추출
            // - Payment 생성 및 결제 처리 시작

            log.info("Successfully processed OrderCreatedEvent")
        } catch (e: Exception) {
            log.error("Failed to process OrderCreatedEvent: {}", e.message, e)
            // TODO: DLQ 처리 또는 재시도 로직
        }
    }

    /**
     * Order 취소 이벤트 처리 - 결제 환불 프로세스 시작
     */
    @KafkaListener(
        topics = ["dev.order-team.order.event.order-canceled.v1"],
        groupId = "payment-service-group",
    )
    fun handleOrderCanceledEvent(jsonPayload: String) {
        try {
            log.info("Received OrderCanceledEvent: {}", jsonPayload)

            // TODO: OrderCanceledEvent를 JSON에서 역직렬화
            // val orderCanceledEvent = kotlinJson.decodeFromString<OrderCanceledEvent>(jsonPayload)

            // TODO: 결제 환불 프로세스 시작 로직 구현
            // - Order 정보 추출
            // - 해당 Payment 조회
            // - 환불 처리 시작

            log.info("Successfully processed OrderCanceledEvent")
        } catch (e: Exception) {
            log.error("Failed to process OrderCanceledEvent: {}", e.message, e)
            // TODO: DLQ 처리 또는 재시도 로직
        }
    }
}
