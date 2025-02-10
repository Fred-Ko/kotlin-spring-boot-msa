package com.ddd.user.application.command

import com.ddd.user.application.dto.command.DeactivateUserCommandDto
import com.ddd.user.application.dto.result.DeactivateUserResult

interface DeactivateUserCommand {
    fun deactivateUser(command: DeactivateUserCommandDto): DeactivateUserResult
}
