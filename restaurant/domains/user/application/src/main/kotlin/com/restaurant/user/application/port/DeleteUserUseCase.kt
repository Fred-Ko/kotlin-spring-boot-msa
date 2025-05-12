package com.restaurant.user.application.port

import com.restaurant.user.application.dto.command.DeleteUserCommand

/**
 * 사용자 삭제 유스케이스 인터페이스 (Rule App-Struct)
 */
interface DeleteUserUseCase {
    fun deleteUser(command: DeleteUserCommand)
}
