package com.ddd.user.application.command.usecase

import com.ddd.support.application.usecase.CommandUseCase
import com.ddd.user.application.command.dto.command.CreateUserCommand
import com.ddd.user.application.command.dto.result.CreateUserResult

interface CreateUserUseCase : CommandUseCase<CreateUserCommand, CreateUserResult>
