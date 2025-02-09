package com.ddd.user.application.handler.command

import com.ddd.user.application.command.DeactivateUserCommand
import com.ddd.user.application.command.dto.result.DeactivateUserResult
import com.ddd.user.application.dto.command.DeactivateUserCommandDto
import com.ddd.user.application.exception.UserApplicationException
import com.ddd.user.domain.port.repository.UserRepository
import java.util.UUID
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Service
class DeactivateUserCommandHandler(private val userRepository: UserRepository) :
        DeactivateUserCommand {
    @Transactional
    override fun deactivateUser(command: DeactivateUserCommandDto): DeactivateUserResult {
        val user =
                userRepository.findById(UUID.fromString(command.id))
                        ?: throw UserApplicationException.UserNotFound(command.id)

        val deactivatedUser = user.deactivateUser()
        userRepository.save(deactivatedUser)

        return DeactivateUserResult(id = command.id)
    }
}
