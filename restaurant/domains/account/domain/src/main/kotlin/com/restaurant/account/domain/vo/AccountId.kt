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
 * Value object representing a unique account identifier.
 * This class wraps a UUID and provides type safety and validation.
 */
@Serializable(with = AccountIdSerializer::class)
@JvmInline
value class AccountId private constructor(
    val value: UUID,
) : JavaSerializable {
    companion object {
        /**
         * Creates a new AccountId from a UUID.
         */
        fun of(uuid: UUID): AccountId = AccountId(uuid)

        /**
         * Creates a new AccountId from a string representation of a UUID.
         * @throws AccountDomainException.Validation if the string is not a valid UUID
         */
        fun ofString(value: String): AccountId =
            try {
                AccountId(UUID.fromString(value))
            } catch (e: IllegalArgumentException) {
                throw AccountDomainException.Validation.InvalidAccountIdFormat(value)
            }

        /**
         * Generates a new random AccountId.
         */
        fun generate(): AccountId = AccountId(UUID.randomUUID())
    }

    override fun toString(): String = value.toString()
}

/**
 * Custom serializer for AccountId to handle UUID serialization
 */
object AccountIdSerializer : KSerializer<AccountId> {
    override val descriptor: SerialDescriptor = PrimitiveSerialDescriptor("AccountId", PrimitiveKind.STRING)

    override fun serialize(
        encoder: Encoder,
        value: AccountId,
    ) {
        encoder.encodeString(value.value.toString())
    }

    override fun deserialize(decoder: Decoder): AccountId {
        val uuidString = decoder.decodeString()
        return AccountId.ofString(uuidString)
    }
}
