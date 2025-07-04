package com.restaurant.payment.infrastructure.mapper

import com.restaurant.payment.domain.aggregate.BankTransfer
import com.restaurant.payment.domain.aggregate.CreditCard
import com.restaurant.payment.domain.aggregate.PaymentMethod
import com.restaurant.payment.domain.vo.AccountNumber
import com.restaurant.payment.domain.vo.BankName
import com.restaurant.payment.domain.vo.CardCvv
import com.restaurant.payment.domain.vo.CardExpiry
import com.restaurant.payment.domain.vo.CardNumber
import com.restaurant.payment.domain.vo.PaymentMethodId
import com.restaurant.payment.domain.vo.UserId
import com.restaurant.payment.infrastructure.entity.BankTransferPaymentMethodEntity
import com.restaurant.payment.infrastructure.entity.CreditCardPaymentMethodEntity
import com.restaurant.payment.infrastructure.entity.PaymentMethodEntity

/**
 * Polymorphic mapping from any PaymentMethodEntity to its corresponding domain model.
 */
fun PaymentMethodEntity.toDomain(): PaymentMethod =
    when (this) {
        is CreditCardPaymentMethodEntity -> this.toDomain()
        is BankTransferPaymentMethodEntity -> this.toDomain()
        else -> throw IllegalArgumentException("Unknown PaymentMethodEntity type: ${this::class.simpleName}")
    }

/**
 * Polymorphic mapping from any PaymentMethod domain model to its corresponding entity.
 */
fun PaymentMethod.toEntity(): PaymentMethodEntity =
    when (this) {
        is CreditCard -> this.toEntity()
        is BankTransfer -> this.toEntity()
    }

/**
 * Maps CreditCardPaymentMethodEntity to CreditCard domain model.
 */
private fun CreditCardPaymentMethodEntity.toDomain(): CreditCard =
    CreditCard(
        paymentMethodId = PaymentMethodId.of(this.domainId),
        userId = UserId.of(this.userId),
        alias = this.alias,
        cardNumber = CardNumber.of(this.cardNumber),
        cardExpiry = CardExpiry.of(this.cardExpiry),
        cardCvv = CardCvv.of(this.cardCvv),
        isDefault = this.isDefault,
        createdAt = this.createdAt,
        updatedAt = this.updatedAt,
        version = 0L, // 기본값 사용
    )

/**
 * Maps CreditCard domain model to CreditCardPaymentMethodEntity.
 */
private fun CreditCard.toEntity(): CreditCardPaymentMethodEntity =
    CreditCardPaymentMethodEntity(
        domainId = this.paymentMethodId.value,
        userId = this.userId.value,
        alias = this.alias,
        isDefault = this.isDefault,
        cardNumber = this.cardNumber.toString(),
        cardExpiry = this.cardExpiry.toString(),
        cardCvv = this.cardCvv.toString(),
    )

/**
 * Maps BankTransferPaymentMethodEntity to BankTransfer domain model.
 */
private fun BankTransferPaymentMethodEntity.toDomain(): BankTransfer =
    BankTransfer(
        paymentMethodId = PaymentMethodId.of(this.domainId),
        userId = UserId.of(this.userId),
        alias = this.alias,
        bankName = BankName.of(this.bankName),
        accountNumber = AccountNumber.of(this.accountNumber),
        isDefault = this.isDefault,
        createdAt = this.createdAt,
        updatedAt = this.updatedAt,
        version = 0L, // 기본값 사용
    )

/**
 * Maps BankTransfer domain model to BankTransferPaymentMethodEntity.
 */
private fun BankTransfer.toEntity(): BankTransferPaymentMethodEntity =
    BankTransferPaymentMethodEntity(
        domainId = this.paymentMethodId.value,
        userId = this.userId.value,
        alias = this.alias,
        isDefault = this.isDefault,
        bankName = this.bankName.value,
        accountNumber = this.accountNumber.getUnmaskedValue(),
    )
