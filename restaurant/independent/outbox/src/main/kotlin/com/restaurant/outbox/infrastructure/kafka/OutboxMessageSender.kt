package com.restaurant.outbox.infrastructure.kafka

import com.restaurant.outbox.application.port.model.OutboxMessage
import com.restaurant.outbox.infrastructure.exception.OutboxException
import mu.KotlinLogging
import org.apache.kafka.clients.producer.ProducerRecord
import org.springframework.kafka.core.KafkaTemplate
import org.springframework.stereotype.Component

private val logger = KotlinLogging.logger {}

/**
 * OutboxMessageSender: Outbox 메시지를 Kafka로 전송하는 역할
 */
@Component
class OutboxMessageSender(
    private val kafkaTemplate: KafkaTemplate<String, ByteArray>,
) {
    fun send(message: OutboxMessage) {
        try {
            val record =
                ProducerRecord(
                    message.topic,
                    message.aggregateId,
                    message.payload,
                )

            message.headers.forEach { (key, value) ->
                record.headers().add(key, value.toByteArray())
            }

            kafkaTemplate.send(record).get()
            logger.debug { "Successfully sent message to topic ${message.topic}" }
        } catch (e: Exception) {
            logger.error(e) { "Failed to send message to topic ${message.topic}" }
            throw OutboxException.KafkaSendException(
                message = "Failed to send message to topic ${message.topic}",
                cause = e,
            )
        }
    }
}
