package com.ddd.user.application.command

import com.ddd.user.application.dto.command.DeleteUserCommandDto
import com.ddd.user.application.command.dto.result.DeleteUserResult

interface DeleteUserCommand {
    fun deleteUser(command: DeleteUserCommandDto): DeleteUserResult
}
