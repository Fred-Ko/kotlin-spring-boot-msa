package com.ddd.libs.outbox.implementation

import com.ddd.libs.outbox.model.OutboxEvent
import com.ddd.libs.outbox.publisher.OutboxEventPublisher
import com.fasterxml.jackson.databind.ObjectMapper
import org.springframework.kafka.core.KafkaTemplate
import org.springframework.stereotype.Component

@Component
class KafkaOutboxEventPublisherImpl(
        private val kafkaTemplate: KafkaTemplate<String, String>,
        private val objectMapper: ObjectMapper
) : OutboxEventPublisher {

    override fun publish(event: OutboxEvent) {
        val topic = "${event.aggregateType}.${event.eventType}".lowercase()
        val message = objectMapper.writeValueAsString(event)

        kafkaTemplate.send(topic, event.aggregateId.toString(), message)
    }
}
