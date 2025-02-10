package com.ddd.user.application.command

import com.ddd.user.application.dto.command.RegisterUserCommandDto
import com.ddd.user.application.dto.result.RegisterUserResult

interface RegisterUserCommand {
    fun registerUser(command: RegisterUserCommandDto): RegisterUserResult
}
