package com.restaurant.independent.outbox.application

import com.restaurant.independent.outbox.application.error.OutboxException
import com.restaurant.independent.outbox.application.event.OutboxDomainEvent
import com.restaurant.independent.outbox.application.port.OutboxMessageRepository
import com.restaurant.independent.outbox.application.port.model.OutboxMessage
import org.slf4j.LoggerFactory
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

/**
 * Outbox를 통해 도메인 이벤트를 발행하는 서비스.
 */
@Service
class OutboxService(
    private val outboxMessageRepository: OutboxMessageRepository,
) {
    private val log = LoggerFactory.getLogger(javaClass)

    /**
     * 단일 도메인 이벤트를 발행합니다.
     * @param event 발행할 도메인 이벤트
     * @return 저장된 Outbox 메시지
     */
    @Transactional
    fun publish(event: OutboxDomainEvent): OutboxMessage {
        try {
            val message = createOutboxMessage(event)
            return outboxMessageRepository.save(message)
        } catch (e: Exception) {
            log.error("Failed to publish domain event", e)
            throw OutboxException.MessageSaveException(
                message = "Failed to publish domain event: ${e.message}",
                cause = e,
            )
        }
    }

    /**
     * 여러 도메인 이벤트를 발행합니다.
     * @param events 발행할 도메인 이벤트 목록
     * @return 저장된 Outbox 메시지 목록
     */
    @Transactional
    fun publishAll(events: List<OutboxDomainEvent>): List<OutboxMessage> {
        try {
            val messages = events.map { createOutboxMessage(it) }
            return outboxMessageRepository.saveAll(messages)
        } catch (e: Exception) {
            log.error("Failed to publish domain events", e)
            throw OutboxException.MessageSaveException(
                message = "Failed to publish domain events: ${e.message}",
                cause = e,
            )
        }
    }

    /**
     * 도메인 이벤트를 Outbox 메시지로 변환합니다.
     */
    private fun createOutboxMessage(event: OutboxDomainEvent): OutboxMessage {
        try {
            return OutboxMessage(
                payload = event.payload,
                topic = event.topic,
                headers = buildHeaders(event),
            )
        } catch (e: Exception) {
            log.error("Failed to create outbox message from domain event", e)
            throw OutboxException.MessageSerializationException(
                message = "Failed to create outbox message from domain event: ${e.message}",
                cause = e,
            )
        }
    }

    /**
     * 도메인 이벤트로부터 메시지 헤더를 생성합니다.
     */
    private fun buildHeaders(event: OutboxDomainEvent): Map<String, String> =
        mapOf(
            "eventId" to event.eventId.toString(),
            "aggregateType" to event.aggregateType,
            "aggregateId" to event.aggregateId,
            "eventType" to event.eventType,
            "eventVersion" to event.eventVersion,
        )
}
