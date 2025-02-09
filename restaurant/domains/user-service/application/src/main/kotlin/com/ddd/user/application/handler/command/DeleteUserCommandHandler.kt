package com.ddd.user.application.handler.command

import com.ddd.user.application.command.DeleteUserCommand
import com.ddd.user.application.command.dto.result.DeleteUserResult
import com.ddd.user.application.dto.command.DeleteUserCommandDto
import com.ddd.user.application.exception.UserApplicationException
import com.ddd.user.domain.port.repository.UserRepository
import java.util.UUID
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Service
class DeleteUserCommandHandler(private val userRepository: UserRepository) : DeleteUserCommand {
    @Transactional
    override fun deleteUser(command: DeleteUserCommandDto): DeleteUserResult {
        val user =
                userRepository.findById(UUID.fromString(command.id))
                        ?: throw UserApplicationException.UserNotFound(command.id)

        userRepository.delete(user)
        return DeleteUserResult(id = command.id)
    }
}
