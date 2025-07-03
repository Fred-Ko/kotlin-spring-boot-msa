package com.restaurant.payment.domain.entity

import com.restaurant.payment.domain.vo.AccountNumber
import com.restaurant.payment.domain.vo.BankName
import com.restaurant.payment.domain.vo.PaymentMethodId
import com.restaurant.payment.domain.vo.PaymentMethodType
import com.restaurant.payment.domain.vo.UserId
import java.time.Instant

/**
 * 계좌이체 결제 수단 구현체
 */
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
) : PaymentMethod {
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
            return BankTransfer(
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
        }
    }

    override fun updateAlias(newAlias: String): BankTransfer = this.copy(alias = newAlias, updatedAt = Instant.now())

    override fun setAsDefault(): BankTransfer = this.copy(isDefault = true, updatedAt = Instant.now())

    override fun unsetAsDefault(): BankTransfer = this.copy(isDefault = false, updatedAt = Instant.now())
}
