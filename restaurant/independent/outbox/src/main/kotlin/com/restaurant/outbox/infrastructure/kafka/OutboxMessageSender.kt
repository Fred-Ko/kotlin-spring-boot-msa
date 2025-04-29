package com.restaurant.outbox.infrastructure.kafka

import com.restaurant.outbox.port.model.OutboxMessage
import org.slf4j.LoggerFactory
import org.springframework.beans.factory.annotation.Qualifier
import org.springframework.kafka.core.KafkaTemplate
import org.springframework.kafka.support.KafkaHeaders
import org.springframework.messaging.support.MessageBuilder
import org.springframework.stereotype.Component

/**
 * OutboxMessageSender: Outbox 메시지를 Kafka로 전송하는 역할
 */
@Component
class OutboxMessageSender(
    @Qualifier("outboxKafkaTemplate")
    private val kafkaTemplate: KafkaTemplate<String, ByteArray>,
) {
    private val log = LoggerFactory.getLogger(javaClass)

    fun send(message: OutboxMessage) {
        val kafkaMessage =
            MessageBuilder
                .withPayload(message.payload)
                .setHeader(KafkaHeaders.TOPIC, message.topic)
                .setHeader(KafkaHeaders.KEY, message.aggregateId)
                .copyHeaders(message.headers)
                .build()

        try {
            val future = kafkaTemplate.send(kafkaMessage)
            future.whenComplete { result, ex ->
                if (ex == null) {
                    log.info(
                        "Kafka send success: topic={}, key={}, offset={}",
                        message.topic,
                        message.aggregateId,
                        result?.recordMetadata?.offset(),
                    )
                } else {
                    log.error("Kafka send failed: topic={}, key={}", message.topic, message.aggregateId, ex)
                    throw ex
                }
            }
        } catch (e: Exception) {
            log.error("Exception during Kafka send: topic={}, key={}", message.topic, message.aggregateId, e)
            throw e
        }
    }
}
