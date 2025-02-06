package com.ddd.user.application.command.usecase

import com.ddd.user.application.command.dto.command.UpdateUserCommand
import com.ddd.user.application.command.dto.result.UpdateUserResult
import com.ddd.support.application.usecase.CommandUseCase

interface UpdateUserUseCase : CommandUseCase<UpdateUserCommand, UpdateUserResult>
