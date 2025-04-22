package com.restaurant.infrastructure.user.repository

import com.restaurant.common.domain.event.DomainEvent
import com.restaurant.common.infrastructure.avro.envelope.EventEnvelope
import com.restaurant.domain.user.aggregate.User
import com.restaurant.domain.user.repository.UserRepository
import com.restaurant.domain.user.vo.Email
import com.restaurant.domain.user.vo.UserId
import com.restaurant.independent.outbox.application.port.OutboxMessageRepository
import com.restaurant.independent.outbox.application.port.model.OutboxMessage
import com.restaurant.infrastructure.user.avro.UserEventPayload
import com.restaurant.infrastructure.user.extensions.toDomain
import com.restaurant.infrastructure.user.extensions.toEntity
import org.apache.avro.io.EncoderFactory
import org.apache.avro.specific.SpecificDatumWriter
import org.springframework.stereotype.Repository
import java.io.ByteArrayOutputStream
import java.nio.ByteBuffer
import java.time.Instant
import java.util.UUID
import java.util.concurrent.TimeUnit
import com.restaurant.infrastructure.user.avro.Address as AvroAddress

@Repository
class UserRepositoryImpl(
    private val jpaRepository: SpringDataJpaUserRepository,
    private val outboxMessageRepository: OutboxMessageRepository,
) : UserRepository {
    override fun save(user: User): User {
        val domainEvents = user.getDomainEvents().toList()

        val entity = user.toEntity()
        val savedEntity = jpaRepository.save(entity)

        if (domainEvents.isNotEmpty()) {
            val aggregateId = user.id.value.toString()
            val aggregateType = "User"
            val correlationId = UUID.randomUUID().toString() // TODO: MDC에서 가져오기

            val outboxMessages =
                domainEvents.map { domainEvent ->
                    // 1. Create the UserEventPayload (Event content)
                    val avroPayload = createAvroPayload(user)
                    val payloadBytes = serializeAvroPayload(avroPayload)

                    // 2. Create the Envelope with metadata and the serialized payload
                    val envelope =
                        createEnvelope(
                            eventId = correlationId,
                            timestamp = Instant.now(),
                            source = "user",
                            aggregateType = aggregateType,
                            aggregateId = aggregateId,
                            eventType = domainEvent::class.java.simpleName,
                            payloadBytes = payloadBytes,
                        )

                    // 3. Serialize the Envelope
                    val envelopeBytes = serializeEnvelope(envelope)

                    // 4. Determine Kafka topic
                    val topic = resolveTopic(domainEvent)

                    // 5. Create headers for Kafka message (duplicating some envelope data for filtering without deserializing)
                    val headers =
                        mapOf(
                            "correlationId" to correlationId,
                            "aggregateType" to aggregateType,
                            "aggregateId" to aggregateId,
                            "eventType" to domainEvent::class.java.simpleName,
                            "eventId" to correlationId,
                            "timestamp" to Instant.now().toString(),
                        )

                    // 6. Create OutboxMessage with full Envelope payload
                    OutboxMessage(
                        payload = envelopeBytes,
                        topic = topic,
                        headers = headers,
                        aggregateId = aggregateId,
                        aggregateType = aggregateType,
                    )
                }
            outboxMessageRepository.saveAll(outboxMessages)
        }

        user.clearDomainEvents()
        return savedEntity.toDomain()
    }

    override fun findById(id: UserId): User? = jpaRepository.findByDomainId(id.value)?.toDomain()

    override fun findByEmail(email: Email): User? = jpaRepository.findByEmail(email.value)?.toDomain()

    override fun existsByEmail(email: Email): Boolean = jpaRepository.existsByEmail(email.value)

    override fun delete(user: User) {
        jpaRepository.deleteByDomainId(user.id.value)
    }

    private fun createAvroPayload(user: User): UserEventPayload =
        UserEventPayload
            .newBuilder()
            .setUserId(user.id.value.toString())
            .setEmail(user.email.value)
            .setName(user.name.value)
            .setAddresses(
                user.addresses.map { address ->
                    AvroAddress
                        .newBuilder()
                        .setAddressId(address.id.value.toString())
                        .setZipCode(address.zipCode)
                        .setStreet(address.street)
                        .setDetail(address.detail)
                        .setIsDefault(address.isDefault)
                        .build()
                },
            ).build()

    private fun serializeAvroPayload(payload: UserEventPayload): ByteArray {
        val writer = SpecificDatumWriter(UserEventPayload::class.java)
        val out = ByteArrayOutputStream()
        val encoder = EncoderFactory.get().binaryEncoder(out, null)
        writer.write(payload, encoder)
        encoder.flush()
        out.close()
        return out.toByteArray()
    }

    private fun createEnvelope(
        eventId: String,
        timestamp: Instant,
        source: String,
        aggregateType: String,
        aggregateId: String,
        eventType: String,
        payloadBytes: ByteArray,
    ): EventEnvelope {
        // Convert payload bytes to ByteBuffer as required by Avro
        val payloadBuffer = ByteBuffer.wrap(payloadBytes)

        // Create metadata map if needed
        val metadata =
            mapOf(
                "createdAt" to timestamp.toString(),
                "eventType" to eventType,
            )

        return EventEnvelope
            .newBuilder()
            .setSchemaVersion(1)
            .setEventId(eventId)
            .setTimestamp(TimeUnit.SECONDS.toNanos(timestamp.epochSecond) + timestamp.nano)
            .setSource(source)
            .setAggregateType(aggregateType)
            .setAggregateId(aggregateId)
            .setEventType(eventType)
            .setPayload(payloadBuffer)
            .setMetadata(metadata)
            .build()
    }

    private fun serializeEnvelope(envelope: EventEnvelope): ByteArray {
        val writer = SpecificDatumWriter(EventEnvelope::class.java)
        val out = ByteArrayOutputStream()
        val encoder = EncoderFactory.get().binaryEncoder(out, null)
        writer.write(envelope, encoder)
        encoder.flush()
        out.close()
        return out.toByteArray()
    }

    private fun resolveTopic(event: DomainEvent): String {
        val environment = "dev" // TODO: 설정에서 가져오기
        val domain = "user"
        val eventType = "domain-event"
        val entity = "user"
        val version = "v1"
        return "$environment.$domain.$eventType.$entity.$version"
    }
}
