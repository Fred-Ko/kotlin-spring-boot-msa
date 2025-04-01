package com.restaurant.application.user.command.handler

import com.restaurant.application.user.command.UpdateProfileCommand
import com.restaurant.application.user.common.UserErrorCode
import com.restaurant.common.core.command.CommandResult
import com.restaurant.domain.user.repository.UserRepository
import com.restaurant.domain.user.vo.Name
import com.restaurant.domain.user.vo.UserId
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import java.util.UUID

@Service
class UpdateProfileCommandHandler(
    private val userRepository: UserRepository,
) {
    @Transactional
    fun handle(command: UpdateProfileCommand): CommandResult {
        try {
            val userId = UserId(command.userId)
            val user =
                userRepository.findById(userId)
                    ?: return CommandResult(false, errorCode = UserErrorCode.NOT_FOUND.code)

            val name = Name.of(command.name)
            val updatedUser = user.updateProfile(name = name)

            userRepository.save(updatedUser)

            return CommandResult(true, UUID.randomUUID().toString())
        } catch (e: IllegalArgumentException) {
            return CommandResult(false, errorCode = UserErrorCode.INVALID_INPUT.code)
        } catch (e: Exception) {
            return CommandResult(false, errorCode = UserErrorCode.UPDATE_FAILED.code)
        }
    }
}
