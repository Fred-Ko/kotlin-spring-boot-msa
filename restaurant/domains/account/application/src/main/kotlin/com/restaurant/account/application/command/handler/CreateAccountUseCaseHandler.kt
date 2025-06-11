package com.restaurant.account.application.command.handler

import com.restaurant.account.application.command.dto.CreateAccountCommand
import com.restaurant.account.application.command.usecase.CreateAccountUseCase
import com.restaurant.account.domain.aggregate.Account
import com.restaurant.account.domain.repository.AccountRepository
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Service
class CreateAccountUseCaseHandler(
    private val accountRepository: AccountRepository,
) : CreateAccountUseCase {
    @Transactional
    override fun createAccount(command: CreateAccountCommand) {
        val account =
            Account.create(
                userId = command.userId,
            )
        accountRepository.save(account)
    }
}
