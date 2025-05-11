package com.restaurant.user.infrastructure.messaging.serialization

import com.restaurant.outbox.application.port.model.OutboxMessage
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

private val log = KotlinLogging.logger {}

@Component
class OutboxMessageFactory(
    @Value("\${kafka.topics.user-event:dev.user-event}")
    private val userEventTopic: String,
) {
    companion object {
        private const val AGGREGATE_TYPE = "User"
    }

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

        val eventTypeString = userEvent::class.simpleName ?: "UnknownEvent"

        val headers =
            mutableMapOf(
                "correlationId" to (MDC.get("correlationId") ?: userEvent.eventId.toString()),
                "eventType" to eventTypeString,
                "aggregateId" to userEvent.aggregateId,
                "aggregateType" to AGGREGATE_TYPE,
            )

        val outboxMessage =
            OutboxMessage(
                payload = domainPayloadBytes,
                topic = determineTopic(userEvent),
                headers = headers,
                aggregateId = userEvent.aggregateId,
                aggregateType = AGGREGATE_TYPE,
                eventType = eventTypeString,
            )

        return listOf(outboxMessage)
    }

    private fun mapToAvroPayloadDto(event: UserEvent): SpecificRecordBase? =
        try {
            when (event) {
                is UserEvent.Created -> {
                    UserCreated
                        .newBuilder()
                        .setUserId(event.userId.toString())
                        .setUsername(event.username)
                        .setEmail(event.email)
                        .setName(event.name)
                        .setPhoneNumber(event.phoneNumber)
                        .setOccurredAt(event.occurredAt.toEpochMilli())
                        .build()
                }
                is UserEvent.PasswordChanged -> {
                    UserPasswordChanged
                        .newBuilder()
                        .setUserId(event.userId.toString())
                        .setOccurredAt(event.occurredAt.toEpochMilli())
                        .build()
                }
                is UserEvent.ProfileUpdated -> {
                    UserProfileUpdated
                        .newBuilder()
                        .setUserId(event.userId.toString())
                        .setName(event.name)
                        .setPhoneNumber(event.phoneNumber)
                        .setOccurredAt(event.occurredAt.toEpochMilli())
                        .build()
                }
                is UserEvent.AddressRegistered -> {
                    UserAddressAdded
                        .newBuilder()
                        .setUserId(event.userId.toString())
                        .setAddressId(event.address.id)
                        .setName(event.address.name)
                        .setStreetAddress(event.address.streetAddress)
                        .setDetailAddress(event.address.detailAddress)
                        .setCity(event.address.city)
                        .setState(event.address.state)
                        .setCountry(event.address.country)
                        .setZipCode(event.address.zipCode)
                        .setIsDefault(event.address.isDefault)
                        .setOccurredAt(event.occurredAt.toEpochMilli())
                        .build()
                }
                is UserEvent.AddressUpdated -> {
                    UserAddressUpdated
                        .newBuilder()
                        .setUserId(event.userId.toString())
                        .setAddressId(event.address.id)
                        .setName(event.address.name)
                        .setStreetAddress(event.address.streetAddress)
                        .setDetailAddress(event.address.detailAddress)
                        .setCity(event.address.city)
                        .setState(event.address.state)
                        .setCountry(event.address.country)
                        .setZipCode(event.address.zipCode)
                        .setIsDefault(event.address.isDefault)
                        .setOccurredAt(event.occurredAt.toEpochMilli())
                        .build()
                }
                is UserEvent.AddressDeleted -> {
                    UserAddressDeleted
                        .newBuilder()
                        .setUserId(event.userId.toString())
                        .setAddressId(event.addressId)
                        .setOccurredAt(event.occurredAt.toEpochMilli())
                        .build()
                }
                is UserEvent.Withdrawn -> {
                    UserWithdrawn
                        .newBuilder()
                        .setUserId(event.userId.toString())
                        .setOccurredAt(event.occurredAt.toEpochMilli())
                        .build()
                }
            }
        } catch (e: Exception) {
            log.error(e) { "Error mapping UserEvent to Avro DTO: ${event::class.simpleName}" }
            null
        }

    private fun <T : SpecificRecordBase> serializeRecord(record: T): ByteArray? =
        try {
            val writer: DatumWriter<T> = SpecificDatumWriter(record.schema)
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
