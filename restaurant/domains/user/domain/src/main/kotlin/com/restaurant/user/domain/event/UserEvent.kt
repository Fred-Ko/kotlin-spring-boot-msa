package com.restaurant.user.domain.event

import com.restaurant.common.domain.event.DomainEvent
import com.restaurant.user.domain.vo.AddressId
import com.restaurant.user.domain.vo.UserId
import kotlinx.serialization.KSerializer
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import kotlinx.serialization.descriptors.PrimitiveKind
import kotlinx.serialization.descriptors.PrimitiveSerialDescriptor
import kotlinx.serialization.descriptors.SerialDescriptor
import kotlinx.serialization.encoding.Decoder
import kotlinx.serialization.encoding.Encoder
import java.time.Instant
import java.util.UUID

/**
 * Custom serializer for UUID
 */
object UUIDSerializer : KSerializer<UUID> {
    override val descriptor: SerialDescriptor = PrimitiveSerialDescriptor("UUID", PrimitiveKind.STRING)

    override fun serialize(
        encoder: Encoder,
        value: UUID,
    ) {
        encoder.encodeString(value.toString())
    }

    override fun deserialize(decoder: Decoder): UUID = UUID.fromString(decoder.decodeString())
}

/**
 * Custom serializer for Instant
 */
object InstantSerializer : KSerializer<Instant> {
    override val descriptor: SerialDescriptor = PrimitiveSerialDescriptor("Instant", PrimitiveKind.STRING)

    override fun serialize(
        encoder: Encoder,
        value: Instant,
    ) {
        encoder.encodeString(value.toString())
    }

    override fun deserialize(decoder: Decoder): Instant = Instant.parse(decoder.decodeString())
}

/**
 * Sealed class grouping all domain events related to the User aggregate. (Rule 34)
 */
@Serializable
sealed class UserEvent : DomainEvent {
    @Serializable(with = UUIDSerializer::class)
    abstract override val eventId: UUID

    @Serializable(with = InstantSerializer::class)
    abstract override val occurredAt: Instant
    abstract val id: UserId

    override val aggregateId: String
        get() = id.value.toString()
    override val aggregateType: String
        get() = "User"

    /**
     * User Created Event
     */
    @Serializable
    @SerialName("Created")
    data class Created(
        val username: String,
        val email: String,
        val name: String,
        val phoneNumber: String?,
        val userType: String,
        override val id: UserId,
        @Serializable(with = UUIDSerializer::class)
        override val eventId: UUID = UUID.randomUUID(),
        @Serializable(with = InstantSerializer::class)
        override val occurredAt: Instant,
    ) : UserEvent()

    /**
     * User Deleted Event
     */
    @Serializable
    @SerialName("Deleted")
    data class Deleted(
        override val id: UserId,
        @Serializable(with = UUIDSerializer::class)
        override val eventId: UUID = UUID.randomUUID(),
        @Serializable(with = InstantSerializer::class)
        override val occurredAt: Instant,
    ) : UserEvent()

    /**
     * User Password Changed Event
     */
    @Serializable
    @SerialName("PasswordChanged")
    data class PasswordChanged(
        override val id: UserId,
        @Serializable(with = UUIDSerializer::class)
        override val eventId: UUID = UUID.randomUUID(),
        @Serializable(with = InstantSerializer::class)
        override val occurredAt: Instant,
    ) : UserEvent()

    /**
     * User Profile Updated Event
     */
    @Serializable
    @SerialName("ProfileUpdated")
    data class ProfileUpdated(
        val name: String,
        val phoneNumber: String?,
        override val id: UserId,
        @Serializable(with = UUIDSerializer::class)
        override val eventId: UUID = UUID.randomUUID(),
        @Serializable(with = InstantSerializer::class)
        override val occurredAt: Instant,
    ) : UserEvent()

    /**
     * User Address Added Event
     */
    @Serializable
    @SerialName("AddressAdded")
    data class AddressAdded(
        val addressId: AddressId,
        override val id: UserId,
        @Serializable(with = UUIDSerializer::class)
        override val eventId: UUID = UUID.randomUUID(),
        @Serializable(with = InstantSerializer::class)
        override val occurredAt: Instant,
    ) : UserEvent()

    /**
     * User Address Updated Event
     */
    @Serializable
    @SerialName("AddressUpdated")
    data class AddressUpdated(
        val addressId: AddressId,
        val name: String,
        val streetAddress: String,
        val detailAddress: String?,
        val city: String,
        val state: String,
        val country: String,
        val zipCode: String,
        val isDefault: Boolean,
        override val id: UserId,
        @Serializable(with = UUIDSerializer::class)
        override val eventId: UUID = UUID.randomUUID(),
        @Serializable(with = InstantSerializer::class)
        override val occurredAt: Instant,
    ) : UserEvent()

    /**
     * User Address Deleted Event
     */
    @Serializable
    @SerialName("AddressDeleted")
    data class AddressDeleted(
        val addressId: AddressId,
        override val id: UserId,
        @Serializable(with = UUIDSerializer::class)
        override val eventId: UUID = UUID.randomUUID(),
        @Serializable(with = InstantSerializer::class)
        override val occurredAt: Instant,
    ) : UserEvent()

    /**
     * User Withdrawn Event
     */
    @Serializable
    @SerialName("Withdrawn")
    data class Withdrawn(
        override val id: UserId,
        @Serializable(with = UUIDSerializer::class)
        override val eventId: UUID = UUID.randomUUID(),
        @Serializable(with = InstantSerializer::class)
        override val occurredAt: Instant,
    ) : UserEvent()

    /**
     * User Deactivated Event
     */
    @Serializable
    @SerialName("Deactivated")
    data class Deactivated(
        override val id: UserId,
        @Serializable(with = UUIDSerializer::class)
        override val eventId: UUID = UUID.randomUUID(),
        @Serializable(with = InstantSerializer::class)
        override val occurredAt: Instant,
    ) : UserEvent()

    /**
     * User Activated Event
     */
    @Serializable
    @SerialName("Activated")
    data class Activated(
        override val id: UserId,
        @Serializable(with = UUIDSerializer::class)
        override val eventId: UUID = UUID.randomUUID(),
        @Serializable(with = InstantSerializer::class)
        override val occurredAt: Instant,
    ) : UserEvent()

    /**
     * Address Data for events (Rule 33, 34)
     */
    @Serializable
    data class AddressData(
        val id: String,
        val name: String,
        val streetAddress: String,
        val detailAddress: String?,
        val city: String,
        val state: String,
        val country: String,
        val zipCode: String,
        val isDefault: Boolean,
    )
}
