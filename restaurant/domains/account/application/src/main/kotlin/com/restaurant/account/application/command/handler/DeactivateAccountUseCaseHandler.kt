package com.restaurant.account.application.command.handler

import com.restaurant.account.application.command.dto.DeactivateAccountCommand
import com.restaurant.account.application.command.usecase.DeactivateAccountUseCase
import com.restaurant.account.application.exception.AccountApplicationException
import com.restaurant.account.domain.repository.AccountRepository
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Service
class DeactivateAccountUseCaseHandler(
    private val accountRepository: AccountRepository,
) : DeactivateAccountUseCase {
    @Transactional
    override fun deactivateAccount(command: DeactivateAccountCommand) {
        val account =
            accountRepository
                .findByUserId(command.userId)
                .orElseThrow { AccountApplicationException.AccountNotFound(command.userId.toString()) }

        val deactivatedAccount = account.deactivate()

        accountRepository.save(deactivatedAccount)
    }
}
