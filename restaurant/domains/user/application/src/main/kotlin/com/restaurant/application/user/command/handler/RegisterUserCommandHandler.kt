package com.restaurant.application.user.command.handler

import com.restaurant.application.user.command.RegisterUserCommand
import com.restaurant.application.user.common.UserErrorCode
import com.restaurant.common.core.command.CommandResult
import com.restaurant.domain.user.aggregate.User
import com.restaurant.domain.user.repository.UserRepository
import com.restaurant.domain.user.vo.Email
import com.restaurant.domain.user.vo.Name
import com.restaurant.domain.user.vo.Password
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import java.util.UUID

@Service
class RegisterUserCommandHandler(
    private val userRepository: UserRepository,
) {
    @Transactional
    fun handle(command: RegisterUserCommand): CommandResult {
        try {
            val email = Email(command.email)

            if (userRepository.existsByEmail(email)) {
                return CommandResult(false, errorCode = UserErrorCode.DUPLICATE_EMAIL.code)
            }

            val password = Password.of(command.password)
            val name = Name.of(command.name)
            val user = User.create(email, password, name)

            userRepository.save(user)

            return CommandResult(true, UUID.randomUUID().toString())
        } catch (e: IllegalArgumentException) {
            return CommandResult(false, errorCode = UserErrorCode.INVALID_INPUT.code)
        } catch (e: Exception) {
            return CommandResult(false, errorCode = UserErrorCode.SYSTEM_ERROR.code)
        }
    }
}
