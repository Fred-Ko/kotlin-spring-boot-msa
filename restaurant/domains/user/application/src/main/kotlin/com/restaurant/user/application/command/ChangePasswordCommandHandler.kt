package com.restaurant.user.application.command

import com.restaurant.user.application.command.dto.ChangePasswordCommand

interface IChangePasswordCommandHandler {
    fun changePassword(command: ChangePasswordCommand)
}
