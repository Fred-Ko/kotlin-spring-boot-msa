package com.restaurant.payment.infrastructure.entity

import jakarta.persistence.Column
import jakarta.persistence.DiscriminatorValue
import jakarta.persistence.Entity
import java.util.UUID

@Entity
@DiscriminatorValue("BANK_TRANSFER")
class BankTransferPaymentMethodEntity(
    domainId: UUID,
    userId: UUID,
    alias: String,
    isDefault: Boolean,
    @Column(name = "bank_name")
    val bankName: String,
    @Column(name = "account_number")
    val accountNumber: String,
) : PaymentMethodEntity(
        domainId = domainId,
        userId = userId,
        alias = alias,
        isDefault = isDefault,
        type = com.restaurant.payment.domain.vo.PaymentMethodType.BANK_TRANSFER,
    )
