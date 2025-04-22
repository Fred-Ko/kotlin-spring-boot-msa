package com.restaurant.infrastructure.user.repository

import com.restaurant.domain.user.aggregate.User
import com.restaurant.domain.user.event.UserEvent
import com.restaurant.domain.user.repository.UserRepository
import com.restaurant.domain.user.vo.Email
import com.restaurant.domain.user.vo.UserId
import com.restaurant.independent.outbox.application.port.OutboxMessageRepository
import com.restaurant.independent.outbox.application.port.model.OutboxMessage
import com.restaurant.infrastructure.user.extensions.toDomain
import com.restaurant.infrastructure.user.extensions.toEntity
import org.apache.avro.io.EncoderFactory
import org.apache.avro.specific.SpecificDatumWriter
import org.slf4j.LoggerFactory
import org.springframework.stereotype.Repository
import java.io.ByteArrayOutputStream
import java.time.ZoneOffset

@Repository
class UserRepositoryImpl(
    private val jpaRepository: SpringDataJpaUserRepository,
    private val outboxMessageRepository: OutboxMessageRepository,
) : UserRepository {
    private val log = LoggerFactory.getLogger(UserRepositoryImpl::class.java)

    override fun save(user: User): User {
        // Store original domain events
        val domainEvents = user.getDomainEvents().toList()

        // Convert and save the entity
        val entity = user.toEntity()
        val savedEntity = jpaRepository.save(entity)

        // Process domain events
        if (domainEvents.isNotEmpty()) {
            try {
                // Create outbox messages for each event
                val outboxMessages =
                    domainEvents.map { event ->
                        // Common values for all events
                        val correlationId = event.eventId
                        val eventType = event::class.java.simpleName
                        val aggregateId = user.id.value.toString()
                        val aggregateType = "User"
                        val source = "user"
                        val timestamp = event.occurredAt.toInstant(ZoneOffset.UTC).toEpochMilli()

                        // Create the appropriate Avro event payload based on event type
                        val (eventPayloadBytes, topic) =
                            when (event) {
                                is UserEvent.Created -> {
                                    // Create Avro record using generated class from schema
                                    val avroEvent =
                                        com.restaurant.infrastructure.user.avro.event.UserCreatedEvent(
                                            userId = user.id.value.toString(),
                                            email = event.email,
                                            name = event.name,
                                            createdAt = timestamp,
                                        )

                                    // Serialize the event
                                    val payloadBytes = serializeAvroEvent(avroEvent)

                                    // Define topic - follow naming convention: {environment}.{domain}.{event-type}.{entity}.{version}
                                    val topic = "dev.user.domain-event.user.v1"

                                    Pair(payloadBytes, topic)
                                }

                                is UserEvent.ProfileUpdated -> {
                                    val avroEvent =
                                        com.restaurant.infrastructure.user.avro.event.UserProfileUpdatedEvent(
                                            userId = user.id.value.toString(),
                                            name = event.name,
                                            phone = null, // Could add phone if available in the domain event
                                            updatedAt = timestamp,
                                        )

                                    val payloadBytes = serializeAvroEvent(avroEvent)
                                    val topic = "dev.user.domain-event.user.v1"

                                    Pair(payloadBytes, topic)
                                }

                                is UserEvent.PasswordChanged -> {
                                    val avroEvent =
                                        com.restaurant.infrastructure.user.avro.event.UserPasswordChangedEvent(
                                            userId = user.id.value.toString(),
                                            changedAt = timestamp,
                                        )

                                    val payloadBytes = serializeAvroEvent(avroEvent)
                                    val topic = "dev.user.domain-event.user.v1"

                                    Pair(payloadBytes, topic)
                                }

                                is UserEvent.AddressAdded -> {
                                    // Would need address details from the user object
                                    val address = user.addresses.find { it.addressId == event.addressId }
                                    if (address != null) {
                                        val avroEvent =
                                            com.restaurant.infrastructure.user.avro.event.UserAddressAddedEvent(
                                                userId = user.id.value.toString(),
                                                addressId = event.addressId.value.toString(),
                                                nickname = address.detail, // 주소가 nickname 대신 detail을 사용
                                                street = address.street,
                                                city = "", // 모델에 없음
                                                state = "", // 모델에 없음
                                                zipCode = address.zipCode,
                                                country = "", // 모델에 없음
                                                isDefault = address.isDefault,
                                                createdAt = timestamp,
                                            )

                                        val payloadBytes = serializeAvroEvent(avroEvent)
                                        val topic = "dev.user.domain-event.user.v1"

                                        Pair(payloadBytes, topic)
                                    } else {
                                        // Fallback if address not found (shouldn't happen)
                                        val avroEvent =
                                            com.restaurant.infrastructure.user.avro.event.UserAddressAddedEvent(
                                                userId = user.id.value.toString(),
                                                addressId = event.addressId.value.toString(),
                                                nickname = "Unknown",
                                                street = "",
                                                city = "",
                                                state = "",
                                                zipCode = "",
                                                country = "",
                                                isDefault = false,
                                                createdAt = timestamp,
                                            )

                                        val payloadBytes = serializeAvroEvent(avroEvent)
                                        val topic = "dev.user.domain-event.user.v1"

                                        Pair(payloadBytes, topic)
                                    }
                                }

                                is UserEvent.AddressUpdated -> {
                                    val avroEvent =
                                        com.restaurant.infrastructure.user.avro.event.UserAddressUpdatedEvent(
                                            userId = user.id.value.toString(),
                                            addressId = event.addressId.value.toString(),
                                            nickname = null,
                                            street = null,
                                            city = null,
                                            state = null,
                                            zipCode = null,
                                            country = null,
                                            isDefault = null,
                                            updatedAt = timestamp,
                                        )

                                    val payloadBytes = serializeAvroEvent(avroEvent)
                                    val topic = "dev.user.domain-event.user.v1"

                                    Pair(payloadBytes, topic)
                                }

                                is UserEvent.AddressRemoved -> {
                                    val avroEvent =
                                        com.restaurant.infrastructure.user.avro.event.UserAddressRemovedEvent(
                                            userId = user.id.value.toString(),
                                            addressId = event.addressId.value.toString(),
                                            removedAt = timestamp,
                                        )

                                    val payloadBytes = serializeAvroEvent(avroEvent)
                                    val topic = "dev.user.domain-event.user.v1"

                                    Pair(payloadBytes, topic)
                                }

                                else -> {
                                    log.warn("Unknown event type: ${event::class.java.simpleName}")
                                    // Default case to handle potential future events
                                    val topic = "dev.user.domain-event.user.v1"
                                    val payloadBytes = ByteArray(0)
                                    Pair(payloadBytes, topic)
                                }
                            }

                        // Create envelope for the event payload
                        val envelopeEvent =
                            com.restaurant.common.infrastructure.avro.envelope.Envelope(
                                schemaVersion = "v1",
                                eventId = correlationId,
                                timestamp = timestamp,
                                source = source,
                                aggregateType = aggregateType,
                                aggregateId = aggregateId,
                                eventType = eventType,
                                payload = eventPayloadBytes,
                            )

                        // Serialize the envelope
                        val envelopeBytes = serializeAvroEvent(envelopeEvent)

                        // Create OutboxMessage
                        OutboxMessage(
                            payload = envelopeBytes,
                            topic = topic,
                            headers =
                                mapOf(
                                    "correlationId" to correlationId,
                                    "aggregateType" to aggregateType,
                                    "aggregateId" to aggregateId,
                                    "eventType" to eventType,
                                ),
                            aggregateId = aggregateId,
                            aggregateType = aggregateType,
                        )
                    }

                // Save to outbox
                outboxMessageRepository.saveAll(outboxMessages)
            } catch (e: Exception) {
                log.error("Failed to process domain events", e)
            }
        }

        // Clear events and return domain object
        user.clearDomainEvents()
        return savedEntity.toDomain()
    }

    private fun <T> serializeAvroEvent(event: T): ByteArray {
        val writer = SpecificDatumWriter<T>((event as Any)::class.java as Class<T>)
        val out = ByteArrayOutputStream()
        val encoder = EncoderFactory.get().binaryEncoder(out, null)
        writer.write(event, encoder)
        encoder.flush()
        return out.toByteArray()
    }

    override fun findById(id: UserId): User? = jpaRepository.findByDomainId(id.value)?.toDomain()

    override fun findByEmail(email: Email): User? = jpaRepository.findByEmail(email.value)?.toDomain()

    override fun existsByEmail(email: Email): Boolean = jpaRepository.existsByEmail(email.value)

    override fun delete(user: User) {
        jpaRepository.deleteByDomainId(user.id.value)
    }
}
