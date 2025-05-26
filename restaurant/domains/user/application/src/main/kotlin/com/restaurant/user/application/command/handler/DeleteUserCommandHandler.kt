package com.restaurant.user.application.command.handler

import com.restaurant.user.application.command.dto.DeleteUserCommand
import com.restaurant.user.application.command.usecase.DeleteUserUseCase
import com.restaurant.user.application.exception.UserApplicationException
import com.restaurant.user.domain.exception.UserDomainException
import com.restaurant.user.domain.repository.UserRepository
import com.restaurant.user.domain.vo.UserId
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Service
class DeleteUserCommandHandler(
    private val userRepository: UserRepository,
) : DeleteUserUseCase {
    @Transactional
    override fun deleteUser(command: DeleteUserCommand) {
        try {
            val userIdVo = UserId.ofString(command.userId)
            val user = userRepository.findById(userIdVo) ?: throw UserDomainException.User.NotFound(userIdVo.toString())

            val updatedUser = user.withdraw()

            userRepository.save(updatedUser)
        } catch (de: UserDomainException) {
            throw de
        } catch (iae: IllegalArgumentException) {
            throw UserApplicationException.BadRequest("Invalid ID format.", iae)
        } catch (e: Exception) {
            throw UserApplicationException.UnexpectedError(message = "Failed to delete user due to an unexpected error.", cause = e)
        }
    }
}
