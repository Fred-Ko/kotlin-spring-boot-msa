package com.restaurant.user.application.command

import com.restaurant.user.application.command.dto.RegisterUserCommand
import com.restaurant.user.domain.vo.UserId

/**
 * 사용자 등록 커맨드 핸들러 인터페이스
 */
interface IRegisterUserCommandHandler {
    fun register(command: RegisterUserCommand): UserId
}
