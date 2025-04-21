package com.restaurant.independent.outbox.infrastructure.kafka

import com.restaurant.independent.outbox.infrastructure.entity.OutboxMessageEntity
import com.restaurant.independent.outbox.infrastructure.error.OutboxErrorCodes
import com.restaurant.independent.outbox.infrastructure.error.OutboxException
import org.apache.kafka.clients.producer.ProducerRecord
import org.slf4j.LoggerFactory
import org.springframework.kafka.core.KafkaTemplate
import org.springframework.stereotype.Component

/**
 * Outbox 메시지를 Kafka로 전송하는 컴포넌트.
 */
@Component
class OutboxMessageSender(
    private val kafkaTemplate: KafkaTemplate<String, ByteArray>,
) {
    private val log = LoggerFactory.getLogger(javaClass)

    fun send(message: OutboxMessageEntity) {
        try {
            val record =
                ProducerRecord(
                    message.topic,
                    null, // partition
                    message.aggregateId, // key
                    message.payload, // value
                    message.headers.map { (key, value) ->
                        org.apache.kafka.common.header.internals
                            .RecordHeader(key, value.toByteArray())
                    },
                )

            kafkaTemplate
                .send(record)
                .whenComplete { result, ex ->
                    if (ex != null) {
                        log.error("Failed to send message to Kafka. Topic: ${message.topic}, AggregateId: ${message.aggregateId}", ex)
                        throw OutboxException(
                            errorCode = OutboxErrorCodes.KAFKA_SEND_ERROR,
                            message = "Failed to send message to Kafka",
                            cause = ex,
                        )
                    } else {
                        log.debug("Successfully sent message to Kafka. Topic: ${message.topic}, Offset: ${result.recordMetadata.offset()}")
                    }
                }
        } catch (e: Exception) {
            log.error("Error while preparing Kafka message. Topic: ${message.topic}, AggregateId: ${message.aggregateId}", e)
            throw OutboxException(
                errorCode = OutboxErrorCodes.KAFKA_MESSAGE_PREPARATION_ERROR,
                message = "Error while preparing Kafka message",
                cause = e,
            )
        }
    }
}
