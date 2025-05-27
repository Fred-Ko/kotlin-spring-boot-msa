package com.restaurant.user.infrastructure.mapper

import com.github.avrokotlin.avro4k.Avro
import com.restaurant.common.domain.event.DomainEvent
import com.restaurant.outbox.application.dto.OutboxMessage
import com.restaurant.user.domain.event.UserEvent
import com.restaurant.user.domain.vo.AddressId
import com.restaurant.user.domain.vo.UserId
import kotlinx.serialization.ExperimentalSerializationApi
import kotlinx.serialization.KSerializer
import kotlinx.serialization.descriptors.PrimitiveKind
import kotlinx.serialization.descriptors.PrimitiveSerialDescriptor
import kotlinx.serialization.descriptors.SerialDescriptor
import kotlinx.serialization.encoding.Decoder
import kotlinx.serialization.encoding.Encoder
import kotlinx.serialization.modules.SerializersModule
import kotlinx.serialization.modules.contextual
import kotlinx.serialization.serializer
import org.springframework.stereotype.Component
import java.time.Instant
import java.util.UUID

@Component
class DomainEventToOutboxMessageConverter {
    // UUID Serializer
    object UUIDSerializer : KSerializer<UUID> {
        override val descriptor: SerialDescriptor = PrimitiveSerialDescriptor("UUID", PrimitiveKind.STRING)

        override fun serialize(
            encoder: Encoder,
            value: UUID,
        ) = encoder.encodeString(value.toString())

        override fun deserialize(decoder: Decoder): UUID = UUID.fromString(decoder.decodeString())
    }

    // Instant Serializer
    object InstantSerializer : KSerializer<Instant> {
        override val descriptor: SerialDescriptor = PrimitiveSerialDescriptor("Instant", PrimitiveKind.STRING)

        override fun serialize(
            encoder: Encoder,
            value: Instant,
        ) = encoder.encodeString(value.toString())

        override fun deserialize(decoder: Decoder): Instant = Instant.parse(decoder.decodeString())
    }

    // UserId Serializer
    object UserIdSerializer : KSerializer<UserId> {
        override val descriptor: SerialDescriptor = PrimitiveSerialDescriptor("UserId", PrimitiveKind.STRING)

        override fun serialize(
            encoder: Encoder,
            value: UserId,
        ) = encoder.encodeString(value.value.toString())

        override fun deserialize(decoder: Decoder): UserId = UserId.ofString(decoder.decodeString())
    }

    // AddressId Serializer
    object AddressIdSerializer : KSerializer<AddressId> {
        override val descriptor: SerialDescriptor = PrimitiveSerialDescriptor("AddressId", PrimitiveKind.STRING)

        override fun serialize(
            encoder: Encoder,
            value: AddressId,
        ) = encoder.encodeString(value.value.toString())

        override fun deserialize(decoder: Decoder): AddressId = AddressId.ofString(decoder.decodeString())
    }

    // Avro instance with contextual serializers
    private val avro =
        Avro {
            serializersModule =
                SerializersModule {
                    contextual(UUIDSerializer)
                    contextual(InstantSerializer)
                    contextual(UserIdSerializer)
                    contextual(AddressIdSerializer)
                }
        }

    @OptIn(ExperimentalSerializationApi::class)
    fun convert(domainEvent: DomainEvent): OutboxMessage {
        val payload =
            when (domainEvent) {
                is UserEvent -> avro.encodeToByteArray(serializer(), domainEvent)
                else -> throw IllegalArgumentException("Unsupported domain event type: ${domainEvent::class.simpleName}")
            }

        val topic = determineTopic(domainEvent)

        val headers = mutableMapOf<String, String>()
        headers["aggregateId"] = domainEvent.aggregateId
        headers["aggregateType"] = domainEvent.aggregateType
        headers["eventType"] = domainEvent::class.simpleName ?: "UnknownEvent"
        headers["eventId"] = domainEvent.eventId.toString()
        headers["occurredAt"] = domainEvent.occurredAt.toString()
        headers["contentType"] = "application/*+avro"

        return OutboxMessage(
            payload = payload,
            topic = topic,
            headers = headers,
            aggregateType = domainEvent.aggregateType,
            aggregateId = domainEvent.aggregateId,
            eventType = domainEvent::class.simpleName ?: "UnknownEvent",
        )
    }

    private fun determineTopic(event: DomainEvent): String {
        val environment = System.getenv("APP_ENV") ?: "dev"
        var domain = "unknown"
        var entityName = "unknown"
        val eventTypeCategory = "domain-event"
        val version = "v1"

        when (event) {
            is UserEvent -> {
                domain = "user"
                entityName = "user"
            }
        }
        return "$environment.$domain.$eventTypeCategory.$entityName.$version"
    }
}
