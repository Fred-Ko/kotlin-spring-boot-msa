package com.restaurant.common.application.usecase

import com.restaurant.common.application.dto.Command

interface CommandUseCase<COMMAND : Command, RESULT> {
    fun execute(command: COMMAND): RESULT
}
