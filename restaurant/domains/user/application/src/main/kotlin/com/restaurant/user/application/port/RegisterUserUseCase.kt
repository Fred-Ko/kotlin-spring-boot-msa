package com.restaurant.user.application.port

import com.restaurant.user.application.dto.command.RegisterUserCommand
import com.restaurant.user.domain.vo.UserId

/**
 * 사용자 등록 유스케이스 인터페이스 (Rule App-Struct)
 * 사용자 ID(UserId)를 반환하도록 수정
 */
interface RegisterUserUseCase {
    fun register(command: RegisterUserCommand): UserId
}
