package com.ddd.user.application.command.handler

import com.ddd.user.application.command.command.DeleteUserCommand
import com.ddd.user.application.command.result.DeleteUserResult
import com.ddd.user.application.command.usecase.DeleteUserUseCase
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
                            ?: return DeleteUserResult.Failure.UserNotFound(command.id.toString())

            userRepository.delete(user)
            DeleteUserResult.Success(command.id.toString())
        } catch (e: Exception) {
            DeleteUserResult.Failure.ValidationError(e.message ?: "Unknown error")
        }
    }
}
