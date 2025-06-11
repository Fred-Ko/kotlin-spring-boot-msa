package com.restaurant.account.application.command.usecase

import com.restaurant.account.application.command.dto.CreateAccountCommand

fun interface CreateAccountUseCase {
    fun createAccount(command: CreateAccountCommand)
}
