package com.restaurant.common.application.command.usecase

import com.restaurant.common.application.command.dto.Command

interface CommandUseCase<COMMAND : Command, RESULT> {
    fun execute(command: COMMAND): RESULT
}
