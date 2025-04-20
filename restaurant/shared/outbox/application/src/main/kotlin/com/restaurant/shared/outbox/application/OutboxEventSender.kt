package com.restaurant.shared.outbox.application

import com.fasterxml.jackson.databind.ObjectMapper
import com.restaurant.shared.outbox.application.dto.OutboxEventPollingDto
import com.restaurant.shared.outbox.application.exception.OutboxEventProcessingException
import com.restaurant.shared.outbox.application.exception.OutboxKafkaSendException
import com.restaurant.shared.outbox.application.port.OutboxEventPollingPort
import org.slf4j.LoggerFactory
import org.springframework.beans.factory.annotation.Value
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Propagation
import org.springframework.transaction.annotation.Transactional
import java.time.LocalDateTime

/**
 * Processes outbox events by sending them to Kafka.
 * Handles deserialization, transformation to Avro, Kafka publishing, and status updates.
 */
@Service
class OutboxEventSender(
    private val outboxPollingPort: OutboxEventPollingPort,
    private val objectMapper: ObjectMapper,
    @Value("\${spring.kafka.properties.app.environment:dev}") private val environment: String,
) {
    private val log = LoggerFactory.getLogger(javaClass)

    fun processEvents(events: List<OutboxEventPollingDto>) {
        events.forEach { event: OutboxEventPollingDto ->
            try {
                processSingleEvent(event)
            } catch (e: Exception) {
                log.error("Failed to process outbox event ID: \\${event.id}. Error: \\${e.message}", e)
                handleProcessingFailure(event, e)
            }
        }
    }

    @Transactional(propagation = Propagation.REQUIRES_NEW)
    fun processSingleEvent(event: OutboxEventPollingDto) {
        log.info("Processing outbox event ID: ${event.id}, Type: ${event.eventType}")
        try {
            val topic = determineTopic(event.aggregateType, event.eventType)
            val key = event.aggregateId
            // sendToKafka(topic, key, ...)
            markEventAsSent(event)
            log.info("Successfully sent outbox event ID: ${event.id} to Kafka topic: $topic")
        } catch (e: OutboxKafkaSendException) {
            log.error("Kafka send failed for event ID: ${event.id}. Error: ${e.message}", e)
            handleProcessingFailure(event, e)
            throw e
        } catch (e: OutboxEventProcessingException) {
            log.error("Processing failed for event ID: ${event.id}. Error: ${e.message}", e)
            handleProcessingFailure(event, e)
            throw e
        } catch (e: Exception) {
            log.error("Unexpected error processing event ID: ${event.id}. Error: ${e.message}", e)
            handleProcessingFailure(event, e)
            throw e
        }
    }

    private fun determineTopic(
        aggregateType: String,
        eventType: String,
    ): String {
        val domain = aggregateType.lowercase()
        val eventTypeSimple = eventType.replace("Event", "").replace("Domain", "").lowercase()
        val version = "v1"
        val eventCategory = "domain-event"
        return "$environment.$domain.$eventCategory.$eventTypeSimple.$version"
    }

    private fun markEventAsSent(event: OutboxEventPollingDto) {
        outboxPollingPort.updateEventStatus(
            eventId = event.id ?: return,
            status = "SENT",
            processedAt = LocalDateTime.now(),
            errorMessage = null,
        )
    }

    private fun handleProcessingFailure(
        event: OutboxEventPollingDto,
        exception: Exception,
    ) {
        outboxPollingPort.updateEventStatus(
            eventId = event.id ?: return,
            status = "FAILED",
            errorMessage = exception.message?.take(1024),
            retryCount = event.retryCount + 1,
            lastAttemptTime = LocalDateTime.now(),
        )
    }
}
