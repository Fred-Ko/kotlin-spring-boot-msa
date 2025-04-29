package com.restaurant.user.application.usecase

import com.restaurant.user.application.dto.command.DeleteUserCommand
import com.restaurant.user.application.port.input.DeleteUserUseCase
import com.restaurant.user.domain.exception.UserDomainException
import com.restaurant.user.domain.repository.UserRepository
import com.restaurant.user.domain.vo.UserId
import mu.KotlinLogging
import org.springframework.security.crypto.password.PasswordEncoder
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

private val log = KotlinLogging.logger {}

@Service
class DeleteUserCommandHandler(
    private val userRepository: UserRepository,
    private val passwordEncoder: PasswordEncoder,
) : DeleteUserUseCase {
    @Transactional
    override fun deleteUser(command: DeleteUserCommand) {
        val userId = UserId.ofString(command.userId)
        log.info { "Attempting to delete user: $userId" }

        val user =
            userRepository.findById(userId)
                ?: throw UserDomainException.User.NotFound(command.userId)

        if (!passwordEncoder.matches(command.password, user.password.value)) {
            val e = UserDomainException.User.PasswordMismatch()
            log.warn(e) { "User deletion failed for userId ${command.userId}: Incorrect password, errorCode=${e.errorCode.code}" }
            throw e
        }

        val withdrawnUser = user.withdraw()
        userRepository.save(withdrawnUser)
        log.info { "User deleted (withdrawn) successfully: $userId" }
    }
}
