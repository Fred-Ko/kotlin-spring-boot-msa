package com.restaurant.application.user.command.handler

import com.restaurant.application.user.command.LoginCommand
import com.restaurant.application.user.common.UserErrorCode
import com.restaurant.common.core.command.CommandResult
import com.restaurant.domain.user.repository.UserRepository
import com.restaurant.domain.user.vo.Email
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import java.util.UUID

@Service
class LoginCommandHandler(
  private val userRepository: UserRepository,
) {
  @Transactional(readOnly = true)
  fun handle(command: LoginCommand): CommandResult {
    try {
      val email = Email(command.email)
      val user =
        userRepository.findByEmail(email)
          ?: return CommandResult(
            false,
            errorCode = UserErrorCode.INVALID_CREDENTIALS.code,
          )

      if (!user.checkPassword(command.password)) {
        return CommandResult(false, errorCode = UserErrorCode.INVALID_CREDENTIALS.code)
      }

      // 실제로는 여기서 세션이나 JWT 토큰을 생성해야 함
      return CommandResult(true, UUID.randomUUID().toString())
    } catch (e: IllegalArgumentException) {
      return CommandResult(false, errorCode = UserErrorCode.INVALID_INPUT.code)
    } catch (e: Exception) {
      return CommandResult(false, errorCode = UserErrorCode.SYSTEM_ERROR.code)
    }
  }
}
