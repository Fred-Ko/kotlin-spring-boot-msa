package com.restaurant.account.domain.vo

import com.restaurant.account.domain.exception.AccountDomainException
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
 * Value object representing a unique user identifier within the Account context.
 * This class wraps a UUID and provides type safety and validation.
 */
@Serializable(with = UserIdSerializer::class)
@JvmInline
value class UserId private constructor(
    val value: UUID,
) : JavaSerializable {
    companion object {
        fun of(uuid: UUID): UserId = UserId(uuid)

        fun ofString(value: String): UserId =
            try {
                UserId(UUID.fromString(value))
            } catch (e: IllegalArgumentException) {
                throw AccountDomainException.Validation.InvalidUserIdFormat(value)
            }
    }

    override fun toString(): String = value.toString()
}

/**
 * Custom serializer for Account's UserId to handle UUID serialization
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
