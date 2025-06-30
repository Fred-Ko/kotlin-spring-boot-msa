package com.restaurant.payment.domain.entity

import com.restaurant.payment.domain.vo.CardCvv
import com.restaurant.payment.domain.vo.CardExpiry
import com.restaurant.payment.domain.vo.CardNumber
import com.restaurant.payment.domain.vo.PaymentMethodId
import com.restaurant.payment.domain.vo.PaymentMethodType
import com.restaurant.payment.domain.vo.UserId
import java.time.Instant

/**
 * 신용/체크카드 결제 수단 구현체
 */
data class CreditCard private constructor(
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
) : PaymentMethod {
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
            return CreditCard(
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
        }

        fun reconstitute(
            paymentMethodId: PaymentMethodId,
            userId: UserId,
            alias: String,
            cardNumber: CardNumber,
            cardExpiry: CardExpiry,
            cardCvv: CardCvv,
            isDefault: Boolean,
            createdAt: Instant,
            updatedAt: Instant,
            version: Long,
        ): CreditCard =
            CreditCard(
                paymentMethodId = paymentMethodId,
                userId = userId,
                alias = alias,
                cardNumber = cardNumber,
                cardExpiry = cardExpiry,
                cardCvv = cardCvv,
                isDefault = isDefault,
                createdAt = createdAt,
                updatedAt = updatedAt,
                version = version,
            )
    }

    override fun updateAlias(newAlias: String): CreditCard = this.copy(alias = newAlias, updatedAt = Instant.now())

    override fun setAsDefault(): CreditCard = this.copy(isDefault = true, updatedAt = Instant.now())

    override fun unsetAsDefault(): CreditCard = this.copy(isDefault = false, updatedAt = Instant.now())

    fun isExpired(): Boolean = cardExpiry.isExpired()

    fun getCardType(): String = cardNumber.getCardType()

    fun getMaskedCardNumber(): String = cardNumber.toString()
}
