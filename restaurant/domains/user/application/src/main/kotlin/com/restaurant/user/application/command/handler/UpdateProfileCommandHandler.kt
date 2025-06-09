package com.restaurant.user.application.command.handler

import com.restaurant.user.application.command.dto.UpdateProfileCommand
import com.restaurant.user.application.command.usecase.UpdateProfileUseCase
import com.restaurant.user.application.exception.UserApplicationException
import com.restaurant.user.domain.exception.UserDomainException
import com.restaurant.user.domain.repository.UserRepository
import com.restaurant.user.domain.vo.Name
import com.restaurant.user.domain.vo.PhoneNumber
import com.restaurant.user.domain.vo.UserId
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Service
class UpdateProfileCommandHandler(
    private val userRepository: UserRepository,
) : UpdateProfileUseCase {
    @Transactional
    override fun updateProfile(command: UpdateProfileCommand) {
        try {
            val userIdVo = UserId.ofString(command.userId)
            val user = userRepository.findById(userIdVo) ?: throw UserDomainException.User.NotFound(userIdVo.toString())

            val name = Name.of(command.name)
            val phoneNumber = command.phoneNumber?.let { PhoneNumber.of(it) }

            val updatedUser = user.updateProfile(name, phoneNumber)

            userRepository.save(updatedUser)
        } catch (de: UserDomainException) {
            throw de
        } catch (iae: IllegalArgumentException) {
            throw UserApplicationException.BadRequest("Invalid input format.", iae)
        } catch (e: Exception) {
            throw UserApplicationException.UnexpectedError(message = "Failed to update profile due to an unexpected error.", cause = e)
        }
    }
}
