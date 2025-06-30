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
 * Value object representing a unique payment method identifier.
 * This class wraps a UUID and provides type safety and validation.
 */
@Serializable(with = PaymentMethodIdSerializer::class)
@JvmInline
value class PaymentMethodId private constructor(
    val value: UUID,
) : JavaSerializable {
    companion object {
        /**
         * Creates a new PaymentMethodId from a UUID.
         */
        fun of(uuid: UUID): PaymentMethodId = PaymentMethodId(uuid)

        /**
         * Creates a new PaymentMethodId from a string representation of a UUID.
         * @throws PaymentDomainException.Validation if the string is not a valid UUID
         */
        fun ofString(value: String): PaymentMethodId =
            try {
                PaymentMethodId(UUID.fromString(value))
            } catch (e: IllegalArgumentException) {
                throw PaymentDomainException.Validation.InvalidPaymentMethodIdFormat(value)
            }

        /**
         * Generates a new random PaymentMethodId.
         */
        fun generate(): PaymentMethodId = PaymentMethodId(UUID.randomUUID())

        fun fromUUID(value: UUID): PaymentMethodId = PaymentMethodId(value)
    }

    override fun toString(): String = value.toString()
}

/**
 * Custom serializer for PaymentMethodId to handle UUID serialization
 */
object PaymentMethodIdSerializer : KSerializer<PaymentMethodId> {
    override val descriptor: SerialDescriptor = PrimitiveSerialDescriptor("PaymentMethodId", PrimitiveKind.STRING)

    override fun serialize(
        encoder: Encoder,
        value: PaymentMethodId,
    ) {
        encoder.encodeString(value.value.toString())
    }

    override fun deserialize(decoder: Decoder): PaymentMethodId {
        val uuidString = decoder.decodeString()
        return PaymentMethodId.ofString(uuidString)
    }
}
