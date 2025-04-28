package com.restaurant.user.application.usecase

import com.restaurant.user.application.dto.command.UpdateAddressCommand
import com.restaurant.user.application.port.`in`.UpdateAddressUseCase
import com.restaurant.user.domain.entity.Address
import com.restaurant.user.domain.repository.UserRepository
import com.restaurant.user.domain.vo.AddressId
import com.restaurant.user.domain.vo.UserId
import mu.KotlinLogging
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

private val log = KotlinLogging.logger {}

@Service
class UpdateAddressCommandHandler(
    private val userRepository: UserRepository,
) : UpdateAddressUseCase {
    @Transactional
    override fun updateAddress(command: UpdateAddressCommand) {
        log.info { "Updating address for userId=${command.userId}, addressId=${command.addressId}" }

        try {
            val userIdVo = UserId.fromString(command.userId)
            val addressIdVo = AddressId.fromString(command.addressId)
            val user = userRepository.findByIdOrThrow(userIdVo)

            val addressToUpdate =
                Address.create(
                    id = addressIdVo,
                    street = command.street,
                    detail = command.detail,
                    zipCode = command.zipCode,
                    isDefault = command.isDefault,
                )

            val updatedUser = user.updateAddress(addressIdVo, addressToUpdate)

            userRepository.save(updatedUser)
            log.info { "Address updated successfully. userId=${userIdVo.value}, addressId=${addressIdVo.value}" }
        } catch (de: UserDomainException) {
            log.warn(
                de,
            ) { "Domain error during address update for user ${command.userId}: code=${de.errorCode.code}, message=${de.message}" }
            throw de
        } catch (iae: IllegalArgumentException) {
            log.warn(iae) { "Invalid ID format during address update: userId=${command.userId}, addressId=${command.addressId}" }
            throw UserApplicationException.BadRequest("Invalid ID format.", iae)
        } catch (e: Exception) {
            log.error(e) { "Unexpected error updating address for user ${command.userId}: ${e.message}" }
            throw UserApplicationException.UnexpectedError("Failed to update address due to an unexpected error.", e)
        }
    }
}
