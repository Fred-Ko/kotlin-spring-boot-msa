package com.restaurant.user.application.usecase

import com.restaurant.user.application.dto.command.ChangePasswordCommand
import com.restaurant.user.application.port.`in`.ChangePasswordUseCase
import com.restaurant.user.domain.repository.UserRepository
import com.restaurant.user.domain.vo.Password
import com.restaurant.user.domain.vo.UserId
import io.github.oshai.kotlinlogging.KotlinLogging
import org.springframework.security.crypto.password.PasswordEncoder
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

private val log = KotlinLogging.logger {}

@Service
class ChangePasswordCommandHandler(
    private val userRepository: UserRepository,
    private val passwordEncoder: PasswordEncoder,
) : ChangePasswordUseCase {
    @Transactional
    override fun handle(command: ChangePasswordCommand) {
        val userId = UserId.fromUUID(command.userId)
        log.info { "Attempting to change password for user: $userId" }

        val user =
            userRepository.findById(userId)
                ?: throw UserDomainException.User.NotFound(command.userId.toString())

        if (!passwordEncoder.matches(command.currentPassword, user.password.value)) {
            val e = UserDomainException.User.PasswordMismatch()
            log.warn(e) { "Password change failed for userId ${command.userId}: Incorrect current password, errorCode=${e.errorCode.code}" }
            throw e
        }

        val encodedNewPassword = passwordEncoder.encode(command.newPassword)
        val newPasswordVo = Password.of(encodedNewPassword)

        val updatedUser = user.changePassword(newPasswordVo)

        userRepository.save(updatedUser)
        log.info { "Password changed successfully for user: $userId" }
    }
}
