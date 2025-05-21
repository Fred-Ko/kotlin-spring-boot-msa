package com.restaurant.user.application.command.usecase

import com.restaurant.user.application.dto.command.ChangePasswordCommand

interface ChangePasswordUseCase {
    fun changePassword(command: ChangePasswordCommand)
}
