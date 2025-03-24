package com.restaurant.application.user.command.handler

import com.restaurant.application.user.command.RegisterUserCommand
import com.restaurant.application.user.common.UserErrorCode
import com.restaurant.common.core.command.CommandResult
import com.restaurant.domain.user.aggregate.User
import com.restaurant.domain.user.repository.UserRepository
import com.restaurant.domain.user.vo.Email
import com.restaurant.domain.user.vo.Password
import java.util.UUID
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Service
class RegisterUserCommandHandler(private val userRepository: UserRepository) {
    @Transactional
    fun handle(command: RegisterUserCommand): CommandResult {
        try {
            val email = Email(command.email)

            if (userRepository.existsByEmail(email)) {
                return CommandResult(false, errorCode = UserErrorCode.DUPLICATE_EMAIL.code)
            }

            val password = Password.of(command.password)
            val user = User.create(email, password, command.name)

            userRepository.save(user)

            return CommandResult(true, UUID.randomUUID().toString())
        } catch (e: IllegalArgumentException) {
            return CommandResult(false, errorCode = UserErrorCode.INVALID_INPUT.code)
        } catch (e: Exception) {
            return CommandResult(false, errorCode = UserErrorCode.SYSTEM_ERROR.code)
        }
    }
}
