package com.ddd.user.application.command

import com.ddd.user.application.dto.command.ModifyUserCommandDto
import com.ddd.user.application.dto.result.ModifyUserResult

interface ModifyUserCommand {
    fun modifyUser(command: ModifyUserCommandDto): ModifyUserResult
}
