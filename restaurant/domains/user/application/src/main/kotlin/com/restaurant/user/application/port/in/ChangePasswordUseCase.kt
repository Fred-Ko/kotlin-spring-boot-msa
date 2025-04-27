package com.restaurant.user.application.port.`in`

import com.restaurant.user.application.dto.command.ChangePasswordCommand

interface ChangePasswordUseCase {
    fun changePassword(command: ChangePasswordCommand)
}
