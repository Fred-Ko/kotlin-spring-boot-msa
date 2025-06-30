package com.restaurant.payment.domain.event

import com.restaurant.common.domain.event.DomainEvent
import com.restaurant.payment.domain.vo.PaymentId
import com.restaurant.payment.domain.vo.PaymentMethodId
import kotlinx.serialization.KSerializer
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import kotlinx.serialization.descriptors.PrimitiveKind
import kotlinx.serialization.descriptors.PrimitiveSerialDescriptor
import kotlinx.serialization.descriptors.SerialDescriptor
import kotlinx.serialization.encoding.Decoder
import kotlinx.serialization.encoding.Encoder
import java.math.BigDecimal
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
 * Custom serializer for BigDecimal
 */
object BigDecimalSerializer : KSerializer<BigDecimal> {
    override val descriptor: SerialDescriptor = PrimitiveSerialDescriptor("BigDecimal", PrimitiveKind.STRING)

    override fun serialize(
        encoder: Encoder,
        value: BigDecimal,
    ) {
        encoder.encodeString(value.toString())
    }

    override fun deserialize(decoder: Decoder): BigDecimal = BigDecimal(decoder.decodeString())
}

/**
 * Sealed class grouping all domain events related to the Payment aggregate. (Rule 34)
 */
@Serializable
sealed class PaymentEvent : DomainEvent {
    @Serializable(with = UUIDSerializer::class)
    abstract override val eventId: UUID

    @Serializable(with = InstantSerializer::class)
    abstract override val occurredAt: Instant
    abstract val id: PaymentId

    override val aggregateId: String
        get() = id.value.toString()
    override val aggregateType: String
        get() = "Payment"
    override val version: Int
        get() = 1

    /**
     * 결제 요청 이벤트
     */
    @Serializable
    @SerialName("PaymentRequested")
    data class PaymentRequested(
        override val id: PaymentId,
        val orderId: String,
        val userId: String,
        @Serializable(with = BigDecimalSerializer::class)
        val amount: BigDecimal,
        val paymentMethodId: String,
        @Serializable(with = UUIDSerializer::class)
        override val eventId: UUID = UUID.randomUUID(),
        @Serializable(with = InstantSerializer::class)
        override val occurredAt: Instant,
    ) : PaymentEvent()

    /**
     * 결제 승인 이벤트
     */
    @Serializable
    @SerialName("PaymentApproved")
    data class PaymentApproved(
        override val id: PaymentId,
        val orderId: String,
        val userId: String,
        val transactionId: String,
        @Serializable(with = BigDecimalSerializer::class)
        val amount: BigDecimal,
        val paymentMethodId: String,
        @Serializable(with = UUIDSerializer::class)
        override val eventId: UUID = UUID.randomUUID(),
        @Serializable(with = InstantSerializer::class)
        override val occurredAt: Instant,
    ) : PaymentEvent()

    /**
     * 결제 실패 이벤트
     */
    @Serializable
    @SerialName("PaymentFailed")
    data class PaymentFailed(
        override val id: PaymentId,
        val orderId: String,
        val userId: String,
        @Serializable(with = BigDecimalSerializer::class)
        val amount: BigDecimal,
        val paymentMethodId: String,
        val failureReason: String,
        @Serializable(with = UUIDSerializer::class)
        override val eventId: UUID = UUID.randomUUID(),
        @Serializable(with = InstantSerializer::class)
        override val occurredAt: Instant,
    ) : PaymentEvent()

    /**
     * 결제 환불 이벤트
     */
    @Serializable
    @SerialName("PaymentRefunded")
    data class PaymentRefunded(
        override val id: PaymentId,
        val orderId: String,
        val userId: String,
        @Serializable(with = BigDecimalSerializer::class)
        val originalAmount: BigDecimal,
        @Serializable(with = BigDecimalSerializer::class)
        val refundedAmount: BigDecimal,
        val reason: String?,
        @Serializable(with = UUIDSerializer::class)
        override val eventId: UUID = UUID.randomUUID(),
        @Serializable(with = InstantSerializer::class)
        override val occurredAt: Instant,
    ) : PaymentEvent()

    /**
     * 환불 실패 이벤트
     */
    @Serializable
    @SerialName("PaymentRefundFailed")
    data class PaymentRefundFailed(
        override val id: PaymentId,
        val orderId: String,
        val userId: String,
        @Serializable(with = BigDecimalSerializer::class)
        val refundAmount: BigDecimal,
        val failureReason: String,
        @Serializable(with = UUIDSerializer::class)
        override val eventId: UUID = UUID.randomUUID(),
        @Serializable(with = InstantSerializer::class)
        override val occurredAt: Instant,
    ) : PaymentEvent()

    /**
     * 결제 수단 등록 이벤트
     */
    @Serializable
    @SerialName("PaymentMethodRegistered")
    data class PaymentMethodRegistered(
        override val id: PaymentId,
        val userId: String,
        val paymentMethodId: PaymentMethodId,
        val paymentMethodType: String,
        val alias: String?,
        val isDefault: Boolean,
        @Serializable(with = UUIDSerializer::class)
        override val eventId: UUID = UUID.randomUUID(),
        @Serializable(with = InstantSerializer::class)
        override val occurredAt: Instant,
    ) : PaymentEvent()
}
