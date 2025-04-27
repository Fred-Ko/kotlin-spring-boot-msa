package com.restaurant.user.infrastructure.serialization

import com.restaurant.outbox.port.dto.OutboxMessage
import io.github.oshai.kotlinlogging.KotlinLogging
import org.springframework.stereotype.Component

private val log = KotlinLogging.logger {}

/**
 * Factory to create OutboxMessage DTOs.
 * Rule 81, 85, 139
 */
@Component
class OutboxMessageFactory {

    /**
     * Creates an OutboxMessage.
     */
    fun createOutboxMessage(
        aggregateId: String,
        aggregateType: String,
        eventType: String,
        payload: ByteArray, // Expect serialized payload (e.g., Envelope bytes)
        targetTopic: String,
        correlationId: String,
        headers: Map<String, String> // Expect pre-built headers
    ): OutboxMessage {

        log.debug {
            "Creating OutboxMessage: topic=$targetTopic, eventType=$eventType, aggregateId=$aggregateId, correlationId=$correlationId, payloadSize=${payload.size}"
        }

        return OutboxMessage(
            aggregateId = aggregateId,
            aggregateType = aggregateType,
            payload = payload,
            topic = targetTopic,
            headers = headers,
        )
    }

    // REMOVED: serializer dependency
    // REMOVED: getTargetTopic method
    // REMOVED: getAggregateType method
    // REMOVED: getAggregateId method
}
