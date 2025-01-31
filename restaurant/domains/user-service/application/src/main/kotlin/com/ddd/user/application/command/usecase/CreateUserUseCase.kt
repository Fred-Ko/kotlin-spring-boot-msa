package com.ddd.user.application.command.usecase

import com.ddd.user.application.command.command.CreateUserCommand
import com.ddd.user.application.command.result.CreateUserResult
import com.ddd.support.application.usecase.CommandUseCase

interface CreateUserUseCase : CommandUseCase<CreateUserCommand, CreateUserResult>
