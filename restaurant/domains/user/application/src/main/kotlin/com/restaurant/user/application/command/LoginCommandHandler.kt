package com.restaurant.user.application.command

import com.restaurant.user.application.command.dto.LoginCommand
import com.restaurant.user.application.query.dto.LoginResult

/**
 * 로그인 커맨드 핸들러 인터페이스
 */
interface ILoginCommandHandler {
    fun login(command: LoginCommand): LoginResult
}
