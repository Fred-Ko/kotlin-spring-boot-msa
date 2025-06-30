package com.restaurant.payment.domain.vo

import com.restaurant.payment.domain.exception.PaymentDomainException
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
 * Value object representing a unique payment identifier.
 * This class wraps a UUID and provides type safety and validation.
 */
@Serializable(with = PaymentIdSerializer::class)
@JvmInline
value class PaymentId private constructor(
    val value: UUID,
) : JavaSerializable {
    companion object {
        /**
         * Creates a new PaymentId from a UUID.
         */
        fun of(uuid: UUID): PaymentId = PaymentId(uuid)

        /**
         * Creates a new PaymentId from a string representation of a UUID.
         * @throws PaymentDomainException.Validation if the string is not a valid UUID
         */
        fun ofString(value: String): PaymentId =
            try {
                PaymentId(UUID.fromString(value))
            } catch (e: IllegalArgumentException) {
                throw PaymentDomainException.Validation.InvalidPaymentIdFormat(value)
            }

        /**
         * Generates a new random PaymentId.
         */
        fun generate(): PaymentId = PaymentId(UUID.randomUUID())

        fun fromUUID(value: UUID): PaymentId = PaymentId(value)
    }

    override fun toString(): String = value.toString()
}

/**
 * Custom serializer for PaymentId to handle UUID serialization
 */
object PaymentIdSerializer : KSerializer<PaymentId> {
    override val descriptor: SerialDescriptor = PrimitiveSerialDescriptor("PaymentId", PrimitiveKind.STRING)

    override fun serialize(
        encoder: Encoder,
        value: PaymentId,
    ) {
        encoder.encodeString(value.value.toString())
    }

    override fun deserialize(decoder: Decoder): PaymentId {
        val uuidString = decoder.decodeString()
        return PaymentId.ofString(uuidString)
    }
}
