package com.restaurant.user.infrastructure.messaging.serialization

import com.restaurant.common.infrastructure.avro.Envelope
import com.restaurant.outbox.port.model.OutboxMessage
import com.restaurant.user.domain.event.UserEvent
import com.restaurant.user.infrastructure.messaging.avro.event.UserAddressAdded
import com.restaurant.user.infrastructure.messaging.avro.event.UserAddressDeleted
import com.restaurant.user.infrastructure.messaging.avro.event.UserAddressUpdated
import com.restaurant.user.infrastructure.messaging.avro.event.UserCreated
import com.restaurant.user.infrastructure.messaging.avro.event.UserPasswordChanged
import com.restaurant.user.infrastructure.messaging.avro.event.UserProfileUpdated
import com.restaurant.user.infrastructure.messaging.avro.event.UserWithdrawn
import io.github.oshai.kotlinlogging.KotlinLogging
import org.apache.avro.io.BinaryEncoder
import org.apache.avro.io.DatumWriter
import org.apache.avro.io.EncoderFactory
import org.apache.avro.specific.SpecificDatumWriter
import org.apache.avro.specific.SpecificRecordBase
import org.slf4j.MDC
import org.springframework.beans.factory.annotation.Value
import org.springframework.stereotype.Component
import java.io.ByteArrayOutputStream
import java.nio.ByteBuffer

private val log = KotlinLogging.logger {}

@Component
class OutboxMessageFactory(
    @Value("\${kafka.topics.user-event:dev.user.domain-event.user.v1}") private val userEventTopic: String,
) {
    /**
     * Creates a list of OutboxMessage objects from a single DomainEvent.
     * Typically, one event results in one message, but allows for future flexibility.
     */
    fun createMessagesFromEvent(userEvent: UserEvent): List<OutboxMessage> {
        val domainEventPayloadDto: SpecificRecordBase =
            mapToAvroPayloadDto(userEvent) ?: run {
                log.error { "Failed to map UserEvent to Avro DTO: ${userEvent::class.simpleName}" }
                return emptyList()
            }

        val domainPayloadBytes: ByteArray =
            serializeRecord(domainEventPayloadDto) ?: run {
                log.error { "Failed to serialize Avro DTO: ${domainEventPayloadDto::class.simpleName}" }
                return emptyList()
            }

        val envelopeDto =
            com.restaurant.common.infrastructure.avro.Envelope(
                userEvent.eventId.toString(),
                "1.0",
                userEvent.occurredAt.toEpochMilli(),
                "user",
                userEvent.aggregateType,
                userEvent.aggregateId,
                java.nio.ByteBuffer.wrap(domainPayloadBytes),
            )

        val finalPayloadBytes: ByteArray =
            serializeRecord(envelopeDto) ?: run {
                log.error { "Failed to serialize Envelope: ${userEvent.eventId}" }
                return emptyList()
            }

        val targetTopic = determineTopic(userEvent)

        val headers =
            mutableMapOf(
                "correlationId" to (MDC.get("correlationId") ?: "unknown"),
                "eventType" to (userEvent::class.simpleName ?: "UnknownEvent"),
                "aggregateId" to userEvent.aggregateId,
                "aggregateType" to userEvent.aggregateType,
            )

        val outboxMessage =
            OutboxMessage(
                payload = finalPayloadBytes,
                topic = targetTopic,
                headers = headers,
                aggregateId = userEvent.aggregateId,
                aggregateType = userEvent.aggregateType,
            )

        return listOf(outboxMessage)
    }

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
                is UserEvent.PasswordChanged ->
                    UserPasswordChanged(
                        event.userId.value.toString(),
                        event.changedAt.toEpochMilli(),
                    )
                is UserEvent.ProfileUpdated ->
                    UserProfileUpdated(
                        event.userId.value.toString(),
                        event.name,
                        event.phoneNumber,
                        event.updatedAt.toEpochMilli(),
                    )
                is UserEvent.AddressRegistered ->
                    UserAddressAdded(
                        event.userId.value.toString(),
                        event.address.id,
                        event.address.name,
                        event.address.streetAddress,
                        event.address.city,
                        event.address.state,
                        event.address.country,
                        event.address.zipCode,
                        event.address.isDefault,
                        event.occurredAt.toEpochMilli(),
                    )
                is UserEvent.AddressUpdated ->
                    UserAddressUpdated(
                        event.userId.value.toString(),
                        event.address.id,
                        event.address.name,
                        event.address.streetAddress,
                        event.address.city,
                        event.address.state,
                        event.address.country,
                        event.address.zipCode,
                        event.address.isDefault,
                        event.occurredAt.toEpochMilli(),
                    )
                is UserEvent.AddressDeleted ->
                    UserAddressDeleted(
                        event.userId.value.toString(),
                        event.addressId,
                        event.deletedAt.toEpochMilli(),
                    )
                is UserEvent.Withdrawn ->
                    UserWithdrawn(
                        event.userId.value.toString(),
                        event.withdrawnAt.toEpochMilli(),
                    )
                else -> {
                    log.warn { "Unsupported UserEvent type for Avro mapping: ${event::class.simpleName}" }
                    null
                }
            }
        } catch (e: Exception) {
            log.error(e) { "Error mapping UserEvent to Avro DTO: ${event::class.simpleName}" }
            null
        }

    private fun serializeRecord(record: SpecificRecordBase): ByteArray? =
        try {
            val writer: DatumWriter<SpecificRecordBase> = SpecificDatumWriter(record.schema)
            val out = ByteArrayOutputStream()
            val encoder: BinaryEncoder = EncoderFactory.get().binaryEncoder(out, null)
            writer.write(record, encoder)
            encoder.flush()
            out.toByteArray()
        } catch (e: Exception) {
            log.error(e) { "Failed to serialize Avro record: ${record::class.simpleName}" }
            null
        }

    private fun determineTopic(event: UserEvent): String = userEventTopic
}
