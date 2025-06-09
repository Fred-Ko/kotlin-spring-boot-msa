package com.restaurant.user.application.command.usecase

import com.restaurant.user.application.command.dto.LoginCommand
import com.restaurant.user.application.query.dto.LoginResult

/**
 * 로그인 유스케이스 인터페이스
 */
interface LoginUseCase {
    fun login(command: LoginCommand): LoginResult
}
