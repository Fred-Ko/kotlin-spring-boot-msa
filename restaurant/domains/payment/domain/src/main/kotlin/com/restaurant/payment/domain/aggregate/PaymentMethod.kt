package com.restaurant.payment.domain.aggregate

import com.restaurant.common.domain.aggregate.AggregateRoot
import com.restaurant.payment.domain.event.PaymentMethodEvent
import com.restaurant.payment.domain.exception.PaymentDomainException
import com.restaurant.payment.domain.vo.AccountNumber
import com.restaurant.payment.domain.vo.BankName
import com.restaurant.payment.domain.vo.CardCvv
import com.restaurant.payment.domain.vo.CardExpiry
import com.restaurant.payment.domain.vo.CardNumber
import com.restaurant.payment.domain.vo.PaymentMethodId
import com.restaurant.payment.domain.vo.PaymentMethodType
import com.restaurant.payment.domain.vo.UserId
import java.time.Instant

/**
 * PaymentMethod AggregateRoot (Rule 11, 17)
 * 사용자의 결제 수단 정보를 관리하는 AggregateRoot.
 * sealed interface로 정의하여 결제 수단 종류에 따른 다형성을 지원합니다.
 */
sealed class PaymentMethod : AggregateRoot() {
    abstract val paymentMethodId: PaymentMethodId
    abstract val userId: UserId
    abstract val type: PaymentMethodType
    abstract val alias: String
    abstract val isDefault: Boolean
    abstract val createdAt: Instant
    abstract val updatedAt: Instant
    abstract val version: Long

    abstract fun updateAlias(newAlias: String): PaymentMethod

    abstract fun setAsDefault(): PaymentMethod

    abstract fun unsetAsDefault(): PaymentMethod

    /**
     * 결제 수단 소유권 검증
     */
    fun validateOwnership(userId: UserId) {
        if (this.userId != userId) {
            throw PaymentDomainException.PaymentMethod.IdMismatch(
                this.userId.toString(),
                userId.toString(),
            )
        }
    }

    /**
     * 기본 결제 수단 설정 시 도메인 이벤트 발행
     */
    protected fun publishDefaultSetEvent() {
        addDomainEvent(
            PaymentMethodEvent.PaymentMethodSetAsDefault(
                id = this.paymentMethodId,
                userId = this.userId.value.toString(),
                paymentMethodId = this.paymentMethodId.value.toString(),
                paymentMethodType = this.type.name,
                alias = this.alias,
                occurredAt = Instant.now(),
            ),
        )
    }

    /**
     * 결제 수단 업데이트 시 도메인 이벤트 발행
     */
    protected fun publishUpdatedEvent() {
        addDomainEvent(
            PaymentMethodEvent.PaymentMethodUpdated(
                id = this.paymentMethodId,
                userId = this.userId.value.toString(),
                paymentMethodId = this.paymentMethodId.value.toString(),
                paymentMethodType = this.type.name,
                alias = this.alias,
                isDefault = this.isDefault,
                occurredAt = Instant.now(),
            ),
        )
    }
}

/**
 * 신용/체크카드 결제 수단 구현체
 */
@ConsistentCopyVisibility
data class CreditCard internal constructor(
    override val paymentMethodId: PaymentMethodId,
    override val userId: UserId,
    override val alias: String,
    val cardNumber: CardNumber,
    val cardExpiry: CardExpiry,
    val cardCvv: CardCvv,
    override val isDefault: Boolean,
    override val createdAt: Instant,
    override val updatedAt: Instant,
    override val version: Long = 0L,
) : PaymentMethod() {
    override val type: PaymentMethodType = PaymentMethodType.CREDIT_CARD

    companion object {
        fun create(
            paymentMethodId: PaymentMethodId,
            userId: UserId,
            alias: String,
            cardNumber: CardNumber,
            cardExpiry: CardExpiry,
            cardCvv: CardCvv,
            isDefault: Boolean = false,
        ): CreditCard {
            val now = Instant.now()
            val creditCard =
                CreditCard(
                    paymentMethodId = paymentMethodId,
                    userId = userId,
                    alias = alias,
                    cardNumber = cardNumber,
                    cardExpiry = cardExpiry,
                    cardCvv = cardCvv,
                    isDefault = isDefault,
                    createdAt = now,
                    updatedAt = now,
                    version = 0L,
                )

            // 결제 수단 등록 이벤트 발행
            creditCard.addDomainEvent(
                PaymentMethodEvent.PaymentMethodRegistered(
                    id = paymentMethodId,
                    userId = userId.value.toString(),
                    paymentMethodId = paymentMethodId.value.toString(),
                    paymentMethodType = PaymentMethodType.CREDIT_CARD.name,
                    alias = alias,
                    isDefault = isDefault,
                    occurredAt = now,
                ),
            )

            return creditCard
        }
    }

    override fun updateAlias(newAlias: String): CreditCard {
        val updated = this.copy(alias = newAlias, updatedAt = Instant.now())
        updated.publishUpdatedEvent()
        return updated
    }

    override fun setAsDefault(): CreditCard {
        val updated = this.copy(isDefault = true, updatedAt = Instant.now())
        updated.publishDefaultSetEvent()
        return updated
    }

    override fun unsetAsDefault(): CreditCard = this.copy(isDefault = false, updatedAt = Instant.now())

    fun isExpired(): Boolean = cardExpiry.isExpired()

    fun getCardType(): String = cardNumber.getCardType()

    fun getMaskedCardNumber(): String = cardNumber.toString()
}

/**
 * 계좌이체 결제 수단 구현체
 */
@ConsistentCopyVisibility
data class BankTransfer internal constructor(
    override val paymentMethodId: PaymentMethodId,
    override val userId: UserId,
    override val alias: String,
    val bankName: BankName,
    val accountNumber: AccountNumber,
    override val isDefault: Boolean,
    override val createdAt: Instant,
    override val updatedAt: Instant,
    override val version: Long = 0L,
) : PaymentMethod() {
    override val type: PaymentMethodType = PaymentMethodType.BANK_TRANSFER

    companion object {
        fun create(
            paymentMethodId: PaymentMethodId,
            userId: UserId,
            alias: String,
            bankName: BankName,
            accountNumber: AccountNumber,
            isDefault: Boolean = false,
        ): BankTransfer {
            val now = Instant.now()
            val bankTransfer =
                BankTransfer(
                    paymentMethodId = paymentMethodId,
                    userId = userId,
                    alias = alias,
                    bankName = bankName,
                    accountNumber = accountNumber,
                    isDefault = isDefault,
                    createdAt = now,
                    updatedAt = now,
                    version = 0L,
                )

            // 결제 수단 등록 이벤트 발행
            bankTransfer.addDomainEvent(
                PaymentMethodEvent.PaymentMethodRegistered(
                    id = paymentMethodId,
                    userId = userId.value.toString(),
                    paymentMethodId = paymentMethodId.value.toString(),
                    paymentMethodType = PaymentMethodType.BANK_TRANSFER.name,
                    alias = alias,
                    isDefault = isDefault,
                    occurredAt = now,
                ),
            )

            return bankTransfer
        }
    }

    override fun updateAlias(newAlias: String): BankTransfer {
        val updated = this.copy(alias = newAlias, updatedAt = Instant.now())
        updated.publishUpdatedEvent()
        return updated
    }

    override fun setAsDefault(): BankTransfer {
        val updated = this.copy(isDefault = true, updatedAt = Instant.now())
        updated.publishDefaultSetEvent()
        return updated
    }

    override fun unsetAsDefault(): BankTransfer = this.copy(isDefault = false, updatedAt = Instant.now())
}
