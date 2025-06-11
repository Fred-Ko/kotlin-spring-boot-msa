package com.restaurant.account.application.query.handler

import com.restaurant.account.application.query.dto.AccountDto
import com.restaurant.account.application.query.dto.GetAccountByUserIdQuery
import com.restaurant.account.application.query.usecase.GetAccountByUserIdUseCase
import com.restaurant.account.domain.aggregate.Account
import com.restaurant.account.domain.repository.AccountRepository
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Service
class GetAccountByUserIdUseCaseHandler(
    private val accountRepository: AccountRepository,
) : GetAccountByUserIdUseCase {
    @Transactional(readOnly = true)
    override fun getAccountByUserId(query: GetAccountByUserIdQuery): AccountDto? =
        accountRepository
            .findByUserId(query.userId)
            .map { it.toDto() }
            .orElse(null)
}

private fun Account.toDto(): AccountDto =
    AccountDto(
        accountId = this.id.value,
        userId = this.userId.value,
        balance = this.balance.value,
        status = this.status,
        createdAt = this.createdAt,
        updatedAt = this.updatedAt,
    )
