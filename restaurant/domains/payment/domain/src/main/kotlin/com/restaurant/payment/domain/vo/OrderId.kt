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
 * Value object representing a unique order identifier.
 * This class wraps a UUID and provides type safety and validation.
 */
@Serializable(with = OrderIdSerializer::class)
@JvmInline
value class OrderId private constructor(
    val value: UUID,
) : JavaSerializable {
    companion object {
        /**
         * Creates a new OrderId from a UUID.
         */
        fun of(uuid: UUID): OrderId = OrderId(uuid)

        /**
         * Creates a new OrderId from a string representation of a UUID.
         * @throws PaymentDomainException.Validation if the string is not a valid UUID
         */
        fun ofString(value: String): OrderId =
            try {
                OrderId(UUID.fromString(value))
            } catch (e: IllegalArgumentException) {
                throw PaymentDomainException.Validation.InvalidOrderIdFormat(value)
            }

        /**
         * Generates a new random OrderId.
         */
        fun generate(): OrderId = OrderId(UUID.randomUUID())

        fun fromUUID(value: UUID): OrderId = OrderId(value)
    }

    override fun toString(): String = value.toString()
}

/**
 * Custom serializer for OrderId to handle UUID serialization
 */
object OrderIdSerializer : KSerializer<OrderId> {
    override val descriptor: SerialDescriptor = PrimitiveSerialDescriptor("OrderId", PrimitiveKind.STRING)

    override fun serialize(
        encoder: Encoder,
        value: OrderId,
    ) {
        encoder.encodeString(value.value.toString())
    }

    override fun deserialize(decoder: Decoder): OrderId {
        val uuidString = decoder.decodeString()
        return OrderId.ofString(uuidString)
    }
}
