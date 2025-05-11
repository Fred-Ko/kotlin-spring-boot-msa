package com.restaurant.user.application.port.input

import com.restaurant.user.application.dto.command.ChangePasswordCommand

interface ChangePasswordUseCase {
    fun changePassword(command: ChangePasswordCommand)
}
