package com.restaurant.payment.domain.event

import com.restaurant.common.domain.event.DomainEvent
import com.restaurant.payment.domain.vo.PaymentMethodId
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import java.time.Instant
import java.util.UUID

/**
 * Sealed class grouping all domain events related to the PaymentMethod aggregate. (Rule 34)
 */
@Serializable
sealed class PaymentMethodEvent : DomainEvent {
    @Serializable(with = UUIDSerializer::class)
    abstract override val eventId: UUID

    @Serializable(with = InstantSerializer::class)
    abstract override val occurredAt: Instant
    abstract val id: PaymentMethodId

    override val aggregateId: String
        get() = id.value.toString()
    override val aggregateType: String
        get() = "PaymentMethod"
    override val version: Int
        get() = 1

    /**
     * 결제 수단 등록 이벤트
     */
    @Serializable
    @SerialName("PaymentMethodRegistered")
    data class PaymentMethodRegistered(
        override val id: PaymentMethodId,
        val userId: String,
        val paymentMethodId: String,
        val paymentMethodType: String,
        val alias: String,
        val isDefault: Boolean,
        @Serializable(with = UUIDSerializer::class)
        override val eventId: UUID = UUID.randomUUID(),
        @Serializable(with = InstantSerializer::class)
        override val occurredAt: Instant,
    ) : PaymentMethodEvent()

    /**
     * 결제 수단 정보 업데이트 이벤트
     */
    @Serializable
    @SerialName("PaymentMethodUpdated")
    data class PaymentMethodUpdated(
        override val id: PaymentMethodId,
        val userId: String,
        val paymentMethodId: String,
        val paymentMethodType: String,
        val alias: String,
        val isDefault: Boolean,
        @Serializable(with = UUIDSerializer::class)
        override val eventId: UUID = UUID.randomUUID(),
        @Serializable(with = InstantSerializer::class)
        override val occurredAt: Instant,
    ) : PaymentMethodEvent()

    /**
     * 기본 결제 수단 설정 이벤트
     */
    @Serializable
    @SerialName("PaymentMethodSetAsDefault")
    data class PaymentMethodSetAsDefault(
        override val id: PaymentMethodId,
        val userId: String,
        val paymentMethodId: String,
        val paymentMethodType: String,
        val alias: String,
        @Serializable(with = UUIDSerializer::class)
        override val eventId: UUID = UUID.randomUUID(),
        @Serializable(with = InstantSerializer::class)
        override val occurredAt: Instant,
    ) : PaymentMethodEvent()

    /**
     * 결제 수단 삭제 이벤트
     */
    @Serializable
    @SerialName("PaymentMethodDeleted")
    data class PaymentMethodDeleted(
        override val id: PaymentMethodId,
        val userId: String,
        val paymentMethodId: String,
        val paymentMethodType: String,
        val alias: String,
        @Serializable(with = UUIDSerializer::class)
        override val eventId: UUID = UUID.randomUUID(),
        @Serializable(with = InstantSerializer::class)
        override val occurredAt: Instant,
    ) : PaymentMethodEvent()
}
