package com.restaurant.user.application.command.usecase

import com.restaurant.user.application.command.dto.ChangePasswordCommand

interface ChangePasswordUseCase {
    fun changePassword(command: ChangePasswordCommand)
}
