package com.restaurant.application.user.command.handler

import com.restaurant.application.user.command.ChangePasswordCommand
import com.restaurant.application.user.common.UserErrorCode
import com.restaurant.common.core.command.CommandResult
import com.restaurant.domain.user.repository.UserRepository
import com.restaurant.domain.user.vo.UserId
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import java.util.UUID

@Service
class ChangePasswordCommandHandler(
  private val userRepository: UserRepository,
) {
  @Transactional
  fun handle(command: ChangePasswordCommand): CommandResult {
    try {
      val userId = UserId(command.userId)
      val user =
        userRepository.findById(userId)
          ?: return CommandResult(false, errorCode = UserErrorCode.NOT_FOUND.code)

      if (!user.checkPassword(command.currentPassword)) {
        return CommandResult(false, errorCode = UserErrorCode.INVALID_PASSWORD.code)
      }

      user.changePassword(command.newPassword)
      userRepository.save(user)

      return CommandResult(true, UUID.randomUUID().toString())
    } catch (e: IllegalArgumentException) {
      return CommandResult(false, errorCode = UserErrorCode.INVALID_INPUT.code)
    } catch (e: Exception) {
      return CommandResult(false, errorCode = UserErrorCode.SYSTEM_ERROR.code)
    }
  }
}
