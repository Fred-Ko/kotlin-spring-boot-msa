package com.ddd.user.application.command.usecase

import com.ddd.user.application.command.command.UpdateUserCommand
import com.ddd.user.application.command.result.UpdateUserResult
import com.ddd.support.application.usecase.CommandUseCase

interface UpdateUserUseCase : CommandUseCase<UpdateUserCommand, UpdateUserResult>
