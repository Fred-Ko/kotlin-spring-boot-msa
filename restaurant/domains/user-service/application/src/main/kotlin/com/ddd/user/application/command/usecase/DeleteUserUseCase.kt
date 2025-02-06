package com.ddd.user.application.command.usecase

import com.ddd.user.application.command.dto.command.DeleteUserCommand
import com.ddd.user.application.command.dto.result.DeleteUserResult
import com.ddd.support.application.usecase.CommandUseCase

interface DeleteUserUseCase : CommandUseCase<DeleteUserCommand, DeleteUserResult>
