package com.ddd.user.application.command.handler

import com.ddd.user.application.command.dto.command.DeleteUserCommand
import com.ddd.user.application.command.dto.result.DeleteUserResult
import com.ddd.user.application.command.usecase.DeleteUserUseCase
import com.ddd.user.application.exception.UserApplicationException
import com.ddd.user.domain.port.repository.UserRepository
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Service
class DeleteUserUseCaseImpl(private val userRepository: UserRepository) : DeleteUserUseCase {

    @Transactional
    override fun execute(command: DeleteUserCommand): DeleteUserResult {
        return try {
            val user =
                    userRepository.findById(command.id)
                            ?: throw UserApplicationException.UserNotFound(
                                    id = command.id.toString()
                            )

            userRepository.delete(user)
            return DeleteUserResult.from(id = user.id)
        } catch (e: Exception) {
            throw UserApplicationException.DeleteUserFailed(id = command.id.toString(), e)
        }
    }
}
