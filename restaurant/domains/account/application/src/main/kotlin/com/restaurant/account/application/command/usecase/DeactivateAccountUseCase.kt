package com.restaurant.account.application.command.usecase

import com.restaurant.account.application.command.dto.DeactivateAccountCommand

fun interface DeactivateAccountUseCase {
    fun deactivateAccount(command: DeactivateAccountCommand)
}
