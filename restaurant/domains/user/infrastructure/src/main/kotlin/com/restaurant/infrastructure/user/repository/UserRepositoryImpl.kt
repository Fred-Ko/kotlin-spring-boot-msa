package com.restaurant.infrastructure.user.repository

import com.restaurant.common.domain.event.DomainEvent
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
import java.util.UUID
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
                    val avroPayload = createAvroPayload(user)
                    val payload = serializeAvroPayload(avroPayload)
                    val topic = resolveTopic(domainEvent)
                    val headers =
                        mapOf(
                            "correlationId" to correlationId,
                            "aggregateType" to aggregateType,
                            "aggregateId" to aggregateId,
                            "eventType" to domainEvent::class.java.simpleName,
                            "eventId" to UUID.randomUUID().toString(),
                        )
                    OutboxMessage(
                        payload = payload,
                        topic = topic,
                        headers = headers,
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

    private fun resolveTopic(event: DomainEvent): String {
        val environment = "dev" // TODO: 설정에서 가져오기
        val domain = "user"
        val eventType = "domain-event"
        val entity = "user"
        val version = "v1"
        return "$environment.$domain.$eventType.$entity.$version"
    }
}
