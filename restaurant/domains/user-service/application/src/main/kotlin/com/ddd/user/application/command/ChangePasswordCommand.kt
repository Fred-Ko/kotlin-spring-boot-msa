package com.ddd.user.application.command

import com.ddd.user.application.dto.command.ChangePasswordCommandDto
import com.ddd.user.application.dto.result.ChangePasswordResult

interface ChangePasswordCommand {
    fun changePassword(command: ChangePasswordCommandDto): ChangePasswordResult
}
