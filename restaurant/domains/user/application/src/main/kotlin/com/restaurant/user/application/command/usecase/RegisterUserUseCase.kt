package com.restaurant.user.application.command.usecase

import com.restaurant.user.application.command.dto.RegisterUserCommand
import com.restaurant.user.domain.vo.UserId

/**
 * 사용자 등록 유스케이스 인터페이스
 */
interface RegisterUserUseCase {
    fun register(command: RegisterUserCommand): UserId
}
