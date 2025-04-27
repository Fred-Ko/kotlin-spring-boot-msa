package com.restaurant.user.infrastructure.messaging.serialization

import com.restaurant.common.config.filter.CorrelationIdFilter
import com.restaurant.common.core.domain.event.DomainEvent
import com.restaurant.common.infrastructure.avro.dto.Envelope
import com.restaurant.outbox.port.dto.OutboxMessage
import com.restaurant.user.domain.event.UserEvent
import com.restaurant.user.infrastructure.avro.dto.* // Import all user Avro DTOs
import io.github.avro4k.Avro
import io.github.oshai.kotlinlogging.KotlinLogging
import kotlinx.serialization.serializer
import org.springframework.beans.factory.annotation.Value
import org.springframework.stereotype.Component
import java.util.*

private val log = KotlinLogging.logger {}

@Component
class OutboxMessageFactory(
    // Inject Kafka topic name from application properties
    @Value("\${kafka.topics.user-event:dev.user.domain-event.user.v1}") private val userEventTopic: String
) {
    // Default Avro instance configured for avro4k
    private val avro = Avro.default

    /**
     * Creates a list of OutboxMessage objects from a single DomainEvent.
     * Typically, one event results in one message, but allows for future flexibility.
     */
    fun createMessagesFromEvent(event: DomainEvent, correlationId: String): List<OutboxMessage> {
        // Ensure the event is a UserEvent before processing
        val userEvent = event as? UserEvent ?: run {
            log.warn { "Received non-UserEvent domain event type: ${event::class.simpleName}. Skipping outbox message creation." }
            return emptyList()
        }

        // 1. Map DomainEvent to specific Avro Payload DTO
        val payloadDto: Any = mapToAvroPayloadDto(userEvent) ?: run {
             log.error { "Failed to map domain event to Avro DTO for event: ${userEvent::class.simpleName}, aggregateId: ${userEvent.aggregateId}" }
             return emptyList() // Or handle error appropriately
        }

        // 2. Serialize the specific Avro Payload DTO to ByteArray
        val payloadBytes: ByteArray = serializePayload(payloadDto) ?: run {
            log.error { "Failed to serialize Avro payload DTO for event: ${userEvent::class.simpleName}, aggregateId: ${userEvent.aggregateId}" }
             return emptyList() // Or handle error appropriately
        }

        // 3. Create the Envelope DTO (without payload initially)
        // Note: Envelope schema version might need a more robust strategy
        val schemaVersion = payloadDto::class.simpleName + "_V1" // Example versioning
        val envelope = Envelope(
            schemaVersion = schemaVersion,
            eventId = correlationId, // Use correlationId as eventId for tracing
            timestamp = userEvent.occurredAt, // From DomainEvent
            source = "user", // Hardcoded domain source for this factory
            aggregateType = userEvent.aggregateType, // From DomainEvent
            aggregateId = userEvent.aggregateId // From DomainEvent (usually String UUID)
            // payload = payloadBytes // Payload is NOT part of the Avro Envelope DTO itself per Rule 113
        )

        // 4. Serialize the Envelope DTO to ByteArray (this will be the final Kafka message payload)
        // This step seems redundant if the goal is to send the *payloadBytes* created in step 2
        // wrapped in an OUTBOX message structure.
        // Rule 85 says: "Envelope DTO와 이벤트 DTO를 조합하여 ... 최종 Kafka 메시지가 될 raw payload bytes (ByteArray)를 생성합니다"
        // Rule 83 OutboxEventEntity: "이벤트 payload (바이트 배열 ByteArray)"
        // Rule 81 OutboxMessage DTO: "raw payload bytes ByteArray"
        // This implies the Outbox stores the *payload* bytes (step 2), NOT the envelope bytes.
        // The Envelope information should likely go into the OutboxMessage/Entity headers/metadata.
        // Let's store payloadBytes from step 2 in OutboxMessage.payload.
        // val finalPayloadBytes = avro.encodeToByteArray(Envelope.serializer(), envelope) // Incorrect based on re-read rules

        // 5. Determine the target Kafka topic
        val targetTopic = determineTopic(userEvent)

        // 6. Create Kafka message headers (including Envelope metadata)
        val headers = createHeaders(userEvent, correlationId, envelope)

        // 7. Create the OutboxMessage DTO
        val outboxMessage = OutboxMessage(
            aggregateId = userEvent.aggregateId,
            aggregateType = userEvent.aggregateType,
            eventType = userEvent::class.simpleName ?: "UnknownEvent",
            payload = payloadBytes, // Use the serialized DTO bytes (Step 2)
            targetTopic = targetTopic,
            headers = headers // Include Envelope info here
        )

        return listOf(outboxMessage)
    }

    // Maps specific UserEvent subtypes to their corresponding Avro DTOs
    private fun mapToAvroPayloadDto(event: UserEvent): Any? = try {
        when (event) {
            is UserEvent.Created -> UserCreatedEventDtoV1(event.userId.value.toString(), event.username, event.email, event.name, event.phoneNumber, event.userType, event.registeredAt)
            is UserEvent.PasswordChanged -> UserPasswordChangedEventDtoV1(event.userId.value.toString(), event.changedAt)
            is UserEvent.ProfileUpdated -> UserProfileUpdatedEventDtoV1(event.userId.value.toString(), event.name, event.phoneNumber, event.updatedAt)
            is UserEvent.AddressAdded -> UserAddressAddedEventDtoV1(event.userId.value.toString(), event.address.toAvroDto(), event.addedAt)
            is UserEvent.AddressUpdated -> UserAddressUpdatedEventDtoV1(event.userId.value.toString(), event.address.toAvroDto(), event.updatedAt)
            is UserEvent.AddressDeleted -> UserAddressDeletedEventDtoV1(event.userId.value.toString(), event.addressId, event.deletedAt)
            is UserEvent.Withdrawn -> UserWithdrawnEventDtoV1(event.userId.value.toString(), event.withdrawnAt)
            // Add cases for other UserEvent subtypes if they exist
        }
    } catch (e: Exception) {
         log.error(e) { "Error mapping UserEvent to Avro DTO: ${event::class.simpleName}" }
         null
    }


    // Serializes the provided Avro DTO object into a ByteArray
    private fun serializePayload(payloadDto: Any): ByteArray? = try {
        when(payloadDto) {
            // Use kx.serialization's serializer() function
            is UserCreatedEventDtoV1 -> avro.encodeToByteArray(UserCreatedEventDtoV1.serializer(), payloadDto)
            is UserPasswordChangedEventDtoV1 -> avro.encodeToByteArray(UserPasswordChangedEventDtoV1.serializer(), payloadDto)
            is UserProfileUpdatedEventDtoV1 -> avro.encodeToByteArray(UserProfileUpdatedEventDtoV1.serializer(), payloadDto)
            is UserAddressAddedEventDtoV1 -> avro.encodeToByteArray(UserAddressAddedEventDtoV1.serializer(), payloadDto)
            is UserAddressUpdatedEventDtoV1 -> avro.encodeToByteArray(UserAddressUpdatedEventDtoV1.serializer(), payloadDto)
            is UserAddressDeletedEventDtoV1 -> avro.encodeToByteArray(UserAddressDeletedEventDtoV1.serializer(), payloadDto)
            is UserWithdrawnEventDtoV1 -> avro.encodeToByteArray(UserWithdrawnEventDtoV1.serializer(), payloadDto)
            // Add other DTO types here
            else -> throw IllegalArgumentException("Cannot serialize unknown Avro DTO type: ${payloadDto::class.simpleName}")
        }
    } catch (e: Exception) {
        log.error(e) { "Failed to serialize Avro payload DTO: ${payloadDto::class.simpleName}" }
        null
    }

    // Determines the Kafka topic based on the event type (simple example)
    private fun determineTopic(event: UserEvent): String {
        // Can be more sophisticated based on event type if needed
        return userEventTopic
    }

    // Creates the headers map for the Kafka message, including Envelope data
    private fun createHeaders(event: UserEvent, correlationId: String, envelope: Envelope): Map<String, String> {
        return mapOf(
            CorrelationIdFilter.CORRELATION_ID_MDC_KEY to correlationId, // Use constant
            "aggregateId" to event.aggregateId,
            "aggregateType" to event.aggregateType,
            "eventType" to (event::class.simpleName ?: "UnknownEvent"),
            // Include Envelope fields in headers
            "envelope_schemaVersion" to envelope.schemaVersion,
            "envelope_eventId" to envelope.eventId, // This is correlationId again
            "envelope_timestamp" to envelope.timestamp.toString(), // Convert Instant to String
            "envelope_source" to envelope.source,
            "envelope_aggregateType" to envelope.aggregateType, // Duplicates aggregateType?
            "envelope_aggregateId" to envelope.aggregateId // Duplicates aggregateId?
            // Consider if duplicating envelope fields in headers is necessary
        ).filterValues { it != null } // Ensure no null values if any fields are optional
    }

    // Extension function to convert AddressData (from UserEvent) to AddressAvroDto
    // This should be private or internal to this factory/module
    private fun UserEvent.AddressData.toAvroDto() = com.restaurant.user.infrastructure.avro.dto.AddressAvroDto(
        addressId = this.addressId,
        street = this.street,
        detail = this.detail,
        zipCode = this.zipCode,
        isDefault = this.isDefault
    )
} 