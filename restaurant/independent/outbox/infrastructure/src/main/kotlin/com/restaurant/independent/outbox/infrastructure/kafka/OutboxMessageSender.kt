package com.restaurant.independent.outbox.infrastructure.kafka

import com.restaurant.independent.outbox.api.error.OutboxException
import com.restaurant.independent.outbox.application.port.OutboxMessageSenderPort
import com.restaurant.independent.outbox.application.port.model.OutboxMessage
import org.apache.kafka.clients.producer.ProducerRecord
import org.slf4j.LoggerFactory
import org.springframework.kafka.core.KafkaTemplate
import org.springframework.stereotype.Component
import java.util.concurrent.CompletableFuture

/**
 * Outbox 메시지를 Kafka로 전송하는 컴포넌트.
 */
@Component
class OutboxMessageSender(
    private val kafkaTemplate: KafkaTemplate<String, ByteArray>,
) : OutboxMessageSenderPort {
    private val log = LoggerFactory.getLogger(javaClass)

    override fun send(message: OutboxMessage): CompletableFuture<*> {
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

            return kafkaTemplate
                .send(record)
                .whenComplete { result, ex ->
                    if (ex != null) {
                        log.error("Failed to send message to Kafka. Topic: ${message.topic}, AggregateId: ${message.aggregateId}", ex)
                        // Don't throw here, handle failure in Poller or JobError logic
                        // throw OutboxException.KafkaSendFailed(
                        // cause = ex,
                        // )
                    } else {
                        log.debug("Successfully sent message to Kafka. Topic: ${message.topic}, Offset: ${result.recordMetadata.offset()}")
                    }
                }
        } catch (e: Exception) {
            log.error("Error while preparing Kafka message. Topic: ${message.topic}, AggregateId: ${message.aggregateId}", e)
            // Don't throw here either
            // throw OutboxException.KafkaSendFailed(
            // cause = e,
            // )
            // Return a completed future with exception
            return CompletableFuture.failedFuture<Void>(OutboxException.KafkaSendFailed(cause = e))
        }
    }
}
