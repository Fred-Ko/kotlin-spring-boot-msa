package com.restaurant.user.application.command.usecase

import com.restaurant.user.application.command.dto.DeleteUserCommand

/**
 * 사용자 삭제 유스케이스 인터페이스
 */
interface DeleteUserUseCase {
    fun deleteUser(command: DeleteUserCommand)
}
