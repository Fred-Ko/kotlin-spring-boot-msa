package com.restaurant.application.user.command.handler

import com.restaurant.application.user.command.DeleteUserCommand
import com.restaurant.application.user.common.UserErrorCode
import com.restaurant.common.core.command.CommandResult
import com.restaurant.domain.user.repository.UserRepository
import com.restaurant.domain.user.vo.UserId
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import java.util.UUID

@Service
class DeleteUserCommandHandler(
    private val userRepository: UserRepository,
) {
    @Transactional
    fun handle(command: DeleteUserCommand): CommandResult {
        try {
            val userId = UserId(command.userId)
            val user =
                userRepository.findById(userId)
                    ?: return CommandResult(false, errorCode = UserErrorCode.NOT_FOUND.code)

            if (!user.checkPassword(command.password)) {
                return CommandResult(false, errorCode = UserErrorCode.INVALID_CREDENTIALS.code)
            }

            userRepository.delete(user)

            return CommandResult(true, UUID.randomUUID().toString())
        } catch (e: IllegalArgumentException) {
            return CommandResult(false, errorCode = UserErrorCode.INVALID_INPUT.code)
        } catch (e: Exception) {
            return CommandResult(false, errorCode = UserErrorCode.DELETION_FAILED.code)
        }
    }
}
