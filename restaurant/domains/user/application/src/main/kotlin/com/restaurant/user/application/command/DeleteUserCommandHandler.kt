package com.restaurant.user.application.command

import com.restaurant.user.application.command.dto.DeleteUserCommand

/**
 * 사용자 삭제 커맨드 핸들러 인터페이스
 */
interface IDeleteUserCommandHandler {
    fun deleteUser(command: DeleteUserCommand)
}
