package com.restaurant.account.domain.aggregate

import com.restaurant.account.domain.event.AccountEvent
import com.restaurant.account.domain.exception.AccountDomainException
import com.restaurant.account.domain.vo.AccountId
import com.restaurant.account.domain.vo.Balance
import com.restaurant.account.domain.vo.UserId
import com.restaurant.common.domain.aggregate.AggregateRoot
import java.math.BigDecimal
import java.time.Instant

data class Account internal constructor(
    val id: AccountId,
    val userId: UserId,
    val balance: Balance,
    val status: AccountStatus,
    val createdAt: Instant,
    val updatedAt: Instant,
    val version: Long = 0L,
) : AggregateRoot() {
    companion object {
        fun create(userId: UserId): Account {
            val now = Instant.now()
            val account =
                Account(
                    id = AccountId.generate(),
                    userId = userId,
                    balance = Balance.of(BigDecimal.ZERO),
                    status = AccountStatus.ACTIVE,
                    createdAt = now,
                    updatedAt = now,
                    version = 0L,
                )
            account.addDomainEvent(
                AccountEvent.AccountOpened(
                    accountId = account.id,
                    userId = account.userId,
                    occurredAt = now,
                ),
            )
            return account
        }
    }

    fun deposit(amount: Balance): Account {
        if (status != AccountStatus.ACTIVE) {
            throw AccountDomainException.Operation.AccountNotActive(this.id.toString())
        }
        if (amount < Balance.ZERO) {
            throw AccountDomainException.Validation.CannotDepositNegativeAmount(amount.value.toLong())
        }

        val newBalance = this.balance + amount
        val updatedAccount =
            this.copy(
                balance = newBalance,
                updatedAt = Instant.now(),
                version = this.version + 1,
            )

        updatedAccount.addDomainEvent(
            AccountEvent.Deposited(
                accountId = this.id,
                amount = amount.value.toLong(),
                occurredAt = Instant.now(),
            ),
        )
        return updatedAccount
    }

    fun withdraw(amount: Balance): Account {
        if (status != AccountStatus.ACTIVE) {
            throw AccountDomainException.Operation.AccountNotActive(this.id.toString())
        }
        if (amount < Balance.ZERO) {
            throw AccountDomainException.Validation.CannotWithdrawNegativeAmount(amount.value.toLong())
        }
        if (this.balance < amount) {
            throw AccountDomainException.Operation.InsufficientFunds(
                this.id.toString(),
                this.balance.value.toLong(),
                amount.value.toLong(),
            )
        }

        val newBalance = this.balance - amount
        val updatedAccount =
            this.copy(
                balance = newBalance,
                updatedAt = Instant.now(),
                version = this.version + 1,
            )

        updatedAccount.addDomainEvent(
            AccountEvent.Withdrawn(
                accountId = this.id,
                amount = amount.value.toLong(),
                occurredAt = Instant.now(),
            ),
        )
        return updatedAccount
    }

    fun close(): Account {
        if (status == AccountStatus.CLOSED) {
            throw AccountDomainException.Operation.AccountAlreadyClosed(this.id.toString())
        }

        val updatedAccount =
            this.copy(
                status = AccountStatus.CLOSED,
                updatedAt = Instant.now(),
                version = this.version + 1,
            )

        updatedAccount.addDomainEvent(
            AccountEvent.AccountClosed(
                accountId = this.id,
                occurredAt = Instant.now(),
            ),
        )
        return updatedAccount
    }

    fun deactivate(): Account {
        if (status == AccountStatus.INACTIVE || status == AccountStatus.CLOSED) {
            return this
        }

        val updatedAccount =
            this.copy(
                status = AccountStatus.INACTIVE,
                updatedAt = Instant.now(),
                version = this.version + 1,
            )

        updatedAccount.addDomainEvent(
            AccountEvent.AccountDeactivated(
                accountId = this.id,
                occurredAt = Instant.now(),
            ),
        )
        return updatedAccount
    }
}
