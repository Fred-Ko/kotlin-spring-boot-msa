package com.restaurant.user.infrastructure.messaging.serialization

import com.restaurant.common.domain.event.DomainEvent
import com.restaurant.outbox.port.model.OutboxMessage
import com.restaurant.user.domain.event.UserEvent
import com.restaurant.user.infrastructure.messaging.avro.event.UserCreated
import mu.KotlinLogging
import org.apache.avro.io.BinaryEncoder
import org.apache.avro.io.DatumWriter
import org.apache.avro.io.EncoderFactory
import org.apache.avro.specific.SpecificDatumWriter
import org.apache.avro.specific.SpecificRecordBase
import org.springframework.beans.factory.annotation.Value
import org.springframework.stereotype.Component
import java.io.ByteArrayOutputStream
// import com.restaurant.user.infrastructure.messaging.avro.event.UserUpdated (추가 스키마 생성 시 적용)
// import com.restaurant.user.infrastructure.messaging.avro.event.UserDeleted (추가 스키마 생성 시 적용)
// import com.restaurant.user.infrastructure.messaging.avro.event.UserPasswordChanged (추가 스키마 생성 시 적용)
// import com.restaurant.user.infrastructure.messaging.avro.event.UserProfileUpdated (추가 스키마 생성 시 적용)
// import com.restaurant.user.infrastructure.messaging.avro.event.UserAddressAdded (추가 스키마 생성 시 적용)
// import com.restaurant.user.infrastructure.messaging.avro.event.UserAddressUpdated (추가 스키마 생성 시 적용)
// import com.restaurant.user.infrastructure.messaging.avro.event.UserAddressDeleted (추가 스키마 생성 시 적용)
// import com.restaurant.user.infrastructure.messaging.avro.event.UserWithdrawn (추가 스키마 생성 시 적용)
// import com.restaurant.user.infrastructure.messaging.avro.event.AddressData (추가 스키마 생성 시 적용)

private val log = KotlinLogging.logger {}

@Component
class OutboxMessageFactory(
    // Inject Kafka topic name from application properties
    @Value("\${kafka.topics.user-event:dev.user.domain-event.user.v1}") private val userEventTopic: String,
) {
    /**
     * Creates a list of OutboxMessage objects from a single DomainEvent.
     * Typically, one event results in one message, but allows for future flexibility.
     */
    fun createMessagesFromEvent(
        event: DomainEvent,
        correlationId: String,
    ): List<OutboxMessage> {
        // Ensure the event is a UserEvent before processing
        val userEvent =
            event as? UserEvent ?: run {
                log.warn { "Received non-UserEvent domain event type: ${event::class.simpleName}. Skipping outbox message creation." }
                return emptyList()
            }

        // 1. Map DomainEvent to specific Avro Payload DTO (generated Java class)
        val payloadDto: SpecificRecordBase =
            mapToAvroPayloadDto(userEvent) ?: run {
                log.error {
                    "Failed to map domain event to Avro DTO for event: " +
                        "${userEvent::class.simpleName}, aggregateId: ${userEvent.aggregateId}"
                }
                return emptyList() // Or handle error appropriately
            }

        // 2. Serialize the specific Avro Payload DTO to ByteArray (Apache Avro Java API)
        val payloadBytes: ByteArray =
            serializePayload(payloadDto) ?: run {
                log.error {
                    "Failed to serialize Avro payload DTO for event: " +
                        "${userEvent::class.simpleName}, " +
                        "aggregateId: ${userEvent.aggregateId}"
                }
                return emptyList()
            }

        // 3. Create the Envelope metadata as a Map
        val envelopeMetadata =
            mapOf(
                "eventId" to userEvent.eventId.toString(),
                "timestamp" to userEvent.occurredAt.toEpochMilli().toString(),
                "source" to "user",
                "aggregateType" to userEvent.aggregateType,
                "aggregateId" to userEvent.aggregateId,
            )

        // 4. Determine the target Kafka topic
        val targetTopic = determineTopic(userEvent)

        // 5. Create Kafka message headers (including Envelope metadata)
        val headers =
            createHeaders(
                event = userEvent,
                correlationId = correlationId,
                envelopeMetadata = envelopeMetadata,
            )

        // 6. Create the OutboxMessage DTO
        val outboxMessage =
            OutboxMessage(
                payload = payloadBytes,
                topic = targetTopic,
                headers = headers,
                aggregateId = userEvent.aggregateId,
                aggregateType = userEvent.aggregateType,
            )

        return listOf(outboxMessage)
    }

    // Maps specific UserEvent subtypes to their corresponding generated Avro Java DTOs
    private fun mapToAvroPayloadDto(event: UserEvent): SpecificRecordBase? =
        try {
            when (event) {
                is UserEvent.Created ->
                    UserCreated(
                        event.userId.value.toString(),
                        event.username,
                        event.email,
                        event.name,
                        event.phoneNumber,
                        event.registeredAt.toEpochMilli(),
                    )
                // TODO: 나머지 이벤트도 Avro Java 클래스 생성 후 동일 패턴으로 작성
                else -> null
            }
        } catch (e: Exception) {
            log.error(e) { "Error mapping UserEvent to Avro DTO: ${event::class.simpleName}" }
            null
        }

    // Serializes the provided Avro DTO object into a ByteArray (Apache Avro Java API)
    private fun serializePayload(payloadDto: SpecificRecordBase): ByteArray? =
        try {
            val writer: DatumWriter<SpecificRecordBase> = SpecificDatumWriter(payloadDto.schema)
            val out = ByteArrayOutputStream()
            val encoder: BinaryEncoder = EncoderFactory.get().binaryEncoder(out, null)
            writer.write(payloadDto, encoder)
            encoder.flush()
            out.toByteArray()
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
    private fun createHeaders(
        event: UserEvent,
        correlationId: String,
        envelopeMetadata: Map<String, String>,
    ): Map<String, String> {
        val headers =
            mutableMapOf(
                "correlationId" to correlationId.toString(),
                "aggregateId" to event.aggregateId.toString(),
                "aggregateType" to event.aggregateType.toString(),
                "eventType" to (event::class.simpleName ?: "UnknownEvent"),
            )

        // Add envelope metadata with prefixes
        envelopeMetadata.forEach { (key, value) ->
            headers["envelope_$key"] = value
        }

        return headers
    }
}
