package com.restaurant.user.application.command.usecase

import com.restaurant.user.application.dto.command.LoginCommand
import com.restaurant.user.application.dto.query.LoginResult

/**
 * 로그인 유스케이스 인터페이스 (Rule App-Struct)
 * 로그인 결과를 반환하도록 수정 (LoginResult 사용)
 */
interface LoginUseCase {
    fun login(command: LoginCommand): LoginResult
}
