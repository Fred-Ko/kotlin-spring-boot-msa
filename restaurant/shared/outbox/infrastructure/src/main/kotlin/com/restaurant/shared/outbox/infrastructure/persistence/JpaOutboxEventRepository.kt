package com.restaurant.shared.outbox.infrastructure.persistence

import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule
import com.fasterxml.jackson.module.kotlin.KotlinModule
import com.restaurant.shared.outbox.application.event.OutboxDomainEvent // 변경: common 의존성 제거
import com.restaurant.shared.outbox.application.port.OutboxEventRepository
import com.restaurant.shared.outbox.infrastructure.entity.OutboxEventEntity
import com.restaurant.shared.outbox.infrastructure.exception.OutboxDeserializationException
import com.restaurant.shared.outbox.infrastructure.exception.OutboxSerializationException
import org.slf4j.LoggerFactory
import org.springframework.stereotype.Component
import org.springframework.transaction.annotation.Transactional

/**
 * JPA implementation of the OutboxEventRepository port.
 * Responsible for saving domain events as OutboxEventEntity into the database.
 */
@Component // Or @Repository, depending on preference
class JpaOutboxEventRepository(
    private val jpaRepository: SpringDataJpaOutboxEventRepository,
    private val objectMapper: ObjectMapper = ObjectMapper().registerModules(KotlinModule.Builder().build(), JavaTimeModule()),
) : OutboxEventRepository {
    @Transactional
    override fun save(
        events: List<OutboxDomainEvent>, // 변경: common 의존성 제거
        aggregateType: String,
        aggregateId: String,
    ) {
        if (events.isEmpty()) return

        val outboxEntities =
            events.map { domainEvent ->
                createOutboxEntity(domainEvent, aggregateType, aggregateId)
            }
        jpaRepository.saveAll(outboxEntities)
    }

    private fun createOutboxEntity(
        event: OutboxDomainEvent, // 변경: common 의존성 제거
        aggregateType: String,
        aggregateId: String,
    ): OutboxEventEntity {
        val payload = serializePayload(event) // Rule 4 Task 3
        return OutboxEventEntity(
            eventId = java.util.UUID.fromString(event.eventId),
            aggregateType = aggregateType,
            aggregateId = aggregateId,
            eventType = event::class.java.simpleName,
            payload = payload,
            status = OutboxEventEntity.STATUS_PENDING,
            occurredAt = event.occurredAt,
            // retryCount, lastAttemptTime, processedAt, errorMessage are handled by poller/sender
        )
    }

    // Serializes the DomainEvent payload to a String (JSON in this case)
    private fun serializePayload(event: OutboxDomainEvent): String =
        // 변경: common 의존성 제거
        try {
            objectMapper.writeValueAsString(event)
        } catch (e: Exception) {
            // Wrap exception in specific OutboxInfrastructureException
            throw OutboxSerializationException("Failed to serialize DomainEvent payload for eventId: ${event.eventId}", e)
        }

    // Deserialization logic (needed for Poller/Sender later) - Rule 4 Task 4
    // This might need refinement based on how event types are mapped back to classes.
    // A more robust solution might involve storing the FQCN or using a registry.
    fun <T : OutboxDomainEvent> deserializePayload( // 변경: common 의존성 제거
        payload: String,
        eventType: String,
    ): T =
        try {
            // This is a simplistic approach assuming eventType matches the class name
            // Need a robust way to get the Class object based on eventType string
            // For example, maintain a map or query a registry.
            // Placeholder for class resolution:
            val eventClass = resolveEventClass(eventType)

            @Suppress("UNCHECKED_CAST")
            objectMapper.readValue(payload, eventClass) as T
        } catch (e: ClassNotFoundException) {
            throw OutboxDeserializationException("Event class not found for eventType: $eventType", e)
        } catch (e: Exception) {
            // Wrap exception in specific OutboxInfrastructureException
            throw OutboxDeserializationException("Failed to deserialize payload for eventType: $eventType", e)
        }

    // Placeholder for a more robust class resolution mechanism
    @Throws(ClassNotFoundException::class)
    private fun resolveEventClass(eventType: String): Class<*> {
        // TODO: Implement a robust mapping from eventType string to Class object.
        // This might involve configuration, reflection with package scanning, or a predefined map.
        // Example simplistic approach (adjust package as needed):
        val basePackage = "com.restaurant" // Or more specific domain event package
        // Attempt common locations
        val potentialPackages =
            listOf(
                "com.restaurant.domain.user.event",
                "com.restaurant.domain.order.event", // Add other domain event packages
                "com.restaurant.common.domain.event",
            )
        potentialPackages.forEach { pkg ->
            try {
                // Construct FQCN. Handle nested classes if UserEvents.Created format is used
                val className = if (eventType.contains(".")) eventType else "$pkg.$eventType"
                return Class.forName(className)
            } catch (e: ClassNotFoundException) {
                // Try next package
            }
        }
        // If not found in common packages, try a direct lookup (might fail for nested)
        try {
            return Class.forName("$basePackage.$eventType") // Fallback attempt
        } catch (e: ClassNotFoundException) {
            // Final attempt if simple name was passed
            try {
                return Class.forName(eventType)
            } catch (finalE: ClassNotFoundException) {
                log.error("Could not resolve class for eventType '$eventType' in packages: $potentialPackages or directly.")
                throw ClassNotFoundException("Event class not found for type: $eventType", finalE)
            }
        }
    }

    companion object {
        private val log = LoggerFactory.getLogger(JpaOutboxEventRepository::class.java)
    }
}
