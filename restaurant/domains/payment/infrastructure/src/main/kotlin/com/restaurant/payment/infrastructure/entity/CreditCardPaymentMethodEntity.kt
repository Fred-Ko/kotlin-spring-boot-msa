package com.restaurant.payment.infrastructure.entity

import jakarta.persistence.Column
import jakarta.persistence.DiscriminatorValue
import jakarta.persistence.Entity
import java.util.UUID

@Entity
@DiscriminatorValue("CREDIT_CARD")
class CreditCardPaymentMethodEntity(
    domainId: UUID,
    userId: UUID,
    alias: String,
    isDefault: Boolean,
    @Column(name = "card_number")
    val cardNumber: String,
    @Column(name = "card_expiry")
    val cardExpiry: String,
    @Column(name = "card_cvv")
    val cardCvv: String,
) : PaymentMethodEntity(
        domainId = domainId,
        userId = userId,
        alias = alias,
        isDefault = isDefault,
        type = com.restaurant.payment.domain.vo.PaymentMethodType.CREDIT_CARD,
    )
