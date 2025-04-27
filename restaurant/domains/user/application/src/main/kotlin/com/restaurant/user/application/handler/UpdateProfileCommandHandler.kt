package com.restaurant.user.application.handler

import com.restaurant.user.application.dto.command.UpdateProfileCommand
import com.restaurant.user.application.exception.UserApplicationException
import com.restaurant.user.application.port.`in`.UpdateProfileUseCase
import com.restaurant.user.domain.exception.UserDomainException
import com.restaurant.user.domain.repository.UserRepository
import com.restaurant.user.domain.vo.Name
import com.restaurant.user.domain.vo.PhoneNumber
import com.restaurant.user.domain.vo.UserId
import mu.KotlinLogging
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

private val log = KotlinLogging.logger {}

@Service
class UpdateProfileCommandHandler(
    private val userRepository: UserRepository,
) : UpdateProfileUseCase {
    @Transactional
    override fun updateProfile(command: UpdateProfileCommand) {
        log.info { "Attempting to update profile for userId=${command.userId}" }

        try {
            val userId = UserId.fromString(command.userId)
            val user = userRepository.findByIdOrThrow(userId)

            val updatedName = Name.of(command.name)
            val updatedPhoneNumber = command.phoneNumber?.let { PhoneNumber.of(it) }

            val updatedUser =
                user.updateProfile(
                    newName = updatedName,
                    newPhoneNumber = updatedPhoneNumber,
                )

            userRepository.save(updatedUser)
            log.info { "Profile updated successfully for userId=${userId.value}" }
        } catch (de: UserDomainException) {
            log.warn(
                de,
            ) { "Domain error during profile update for userId=${command.userId}: code=${de.errorCode.code}, message=${de.message}" }
            throw de
        } catch (iae: IllegalArgumentException) {
            log.warn(iae) { "Invalid data during profile update for userId=${command.userId}: ${iae.message}" }
            throw UserApplicationException.BadRequest("Invalid profile data format: ${iae.message}", iae)
        } catch (e: Exception) {
            log.error(e) { "Unexpected error during profile update for userId=${command.userId}: ${e.message}" }
            throw UserApplicationException.UnexpectedError(cause = e)
        }
    }
}
