package com.restaurant.user.application.command.handler

import com.restaurant.user.application.dto.command.ChangePasswordCommand
import com.restaurant.user.application.exception.UserApplicationException
import com.restaurant.user.application.port.ChangePasswordUseCase
import com.restaurant.user.domain.exception.UserDomainException
import com.restaurant.user.domain.repository.UserRepository
import com.restaurant.user.domain.vo.Password
import com.restaurant.user.domain.vo.UserId
import org.springframework.security.crypto.password.PasswordEncoder
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Service
class ChangePasswordCommandHandler(
    private val userRepository: UserRepository,
    private val passwordEncoder: PasswordEncoder,
) : ChangePasswordUseCase {
    @Transactional
    override fun changePassword(command: ChangePasswordCommand) {
        try {
            val userId = UserId.ofString(command.userId)
            val user = userRepository.findById(userId) ?: throw UserDomainException.User.NotFound(command.userId)

            val newPassword = Password.of(passwordEncoder.encode(command.newPassword))
            val updatedUser = user.changePassword(newPassword)

            userRepository.save(updatedUser)
        } catch (de: UserDomainException) {
            throw de
        } catch (iae: IllegalArgumentException) {
            throw UserApplicationException.BadRequest("Invalid password data format: ${iae.message}", iae)
        } catch (e: Exception) {
            throw UserApplicationException.UnexpectedError(
                message = "Failed to change password due to an unexpected error.",
                cause = e,
            )
        }
    }
}
