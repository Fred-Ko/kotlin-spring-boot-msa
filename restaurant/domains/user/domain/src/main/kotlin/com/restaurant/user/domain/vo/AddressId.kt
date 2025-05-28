package com.restaurant.user.domain.vo
import com.restaurant.user.domain.exception.UserDomainException
import kotlinx.serialization.KSerializer
import kotlinx.serialization.Serializable
import kotlinx.serialization.descriptors.PrimitiveKind
import kotlinx.serialization.descriptors.PrimitiveSerialDescriptor
import kotlinx.serialization.descriptors.SerialDescriptor
import kotlinx.serialization.encoding.Decoder
import kotlinx.serialization.encoding.Encoder
import java.util.UUID
import java.io.Serializable as JavaSerializable

@Serializable(with = AddressIdSerializer::class)
@JvmInline
value class AddressId private constructor(
    val value: UUID,
) : JavaSerializable {
    companion object {
        fun generate(): AddressId = AddressId(UUID.randomUUID())

        fun ofString(value: String): AddressId =
            try {
                AddressId(UUID.fromString(value))
            } catch (e: IllegalArgumentException) {
                throw UserDomainException.Validation.InvalidAddressIdFormat(value)
            }

        fun of(value: UUID): AddressId = AddressId(value)

        fun fromUUID(value: UUID): AddressId = AddressId(value)
    }

    override fun toString(): String = value.toString()
}

/**
 * Custom serializer for AddressId to handle UUID serialization
 */
object AddressIdSerializer : KSerializer<AddressId> {
    override val descriptor: SerialDescriptor = PrimitiveSerialDescriptor("AddressId", PrimitiveKind.STRING)

    override fun serialize(
        encoder: Encoder,
        value: AddressId,
    ) {
        encoder.encodeString(value.value.toString())
    }

    override fun deserialize(decoder: Decoder): AddressId {
        val uuidString = decoder.decodeString()
        return AddressId.ofString(uuidString)
    }
}
