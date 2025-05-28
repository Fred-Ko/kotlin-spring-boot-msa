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

/**
 * Value object representing a unique user identifier.
 * This class wraps a UUID and provides type safety and validation.
 */
@Serializable(with = UserIdSerializer::class)
@JvmInline
value class UserId private constructor(
    val value: UUID,
) : JavaSerializable {
    companion object {
        /**
         * Creates a new UserId from a UUID.
         */
        fun of(uuid: UUID): UserId = UserId(uuid)

        /**
         * Creates a new UserId from a string representation of a UUID.
         * @throws UserDomainException.Validation if the string is not a valid UUID
         */
        fun ofString(value: String): UserId =
            try {
                UserId(UUID.fromString(value))
            } catch (e: IllegalArgumentException) {
                throw UserDomainException.Validation.InvalidUserIdFormat(value)
            }

        /**
         * Generates a new random UserId.
         */
        fun generate(): UserId = UserId(UUID.randomUUID())

        fun fromUUID(value: UUID): UserId = UserId(value)
    }

    override fun toString(): String = value.toString()
}

/**
 * Custom serializer for UserId to handle UUID serialization
 */
object UserIdSerializer : KSerializer<UserId> {
    override val descriptor: SerialDescriptor = PrimitiveSerialDescriptor("UserId", PrimitiveKind.STRING)

    override fun serialize(
        encoder: Encoder,
        value: UserId,
    ) {
        encoder.encodeString(value.value.toString())
    }

    override fun deserialize(decoder: Decoder): UserId {
        val uuidString = decoder.decodeString()
        return UserId.ofString(uuidString)
    }
}
