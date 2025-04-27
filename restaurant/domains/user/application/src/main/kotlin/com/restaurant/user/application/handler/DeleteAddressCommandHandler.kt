package com.restaurant.user.application.handler

import com.restaurant.user.application.dto.command.DeleteAddressCommand
import com.restaurant.user.application.exception.UserApplicationException
import com.restaurant.user.application.port.`in`.DeleteAddressUseCase
import com.restaurant.user.domain.exception.UserDomainException
import com.restaurant.user.domain.repository.UserRepository
import com.restaurant.user.domain.vo.AddressId
import com.restaurant.user.domain.vo.UserId
import mu.KotlinLogging
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

private val log = KotlinLogging.logger {}

@Service
class DeleteAddressCommandHandler(
    private val userRepository: UserRepository,
) : DeleteAddressUseCase {
    @Transactional
    override fun deleteAddress(command: DeleteAddressCommand) {
        log.info { "Deleting address for userId=${command.userId}, addressId=${command.addressId}" }

        try {
            val userIdVo = UserId.fromString(command.userId)
            val addressIdVo = AddressId.fromString(command.addressId)
            val user = userRepository.findByIdOrThrow(userIdVo)

            val updatedUser = user.deleteAddress(addressIdVo)
            userRepository.save(updatedUser)

            log.info { "Address deleted successfully. userId=${userIdVo.value}, addressId=${addressIdVo.value}" }
        } catch (de: UserDomainException) {
            log.warn(
                de,
            ) { "Domain error during address deletion for user ${command.userId}: code=${de.errorCode.code}, message=${de.message}" }
            throw de
        } catch (iae: IllegalArgumentException) {
            log.warn(iae) { "Invalid ID format during address deletion: userId=${command.userId}, addressId=${command.addressId}" }
            throw UserApplicationException.BadRequest("Invalid ID format.", iae)
        } catch (e: Exception) {
            log.error(e) { "Unexpected error deleting address for user ${command.userId}: ${e.message}" }
            throw UserApplicationException.UnexpectedError("Failed to delete address due to an unexpected error.", e)
        }
    }
}
