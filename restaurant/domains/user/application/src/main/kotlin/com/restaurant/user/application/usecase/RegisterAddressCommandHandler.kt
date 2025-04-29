package com.restaurant.user.application.usecase

import com.restaurant.user.application.dto.command.RegisterAddressCommand
import com.restaurant.user.application.exception.UserApplicationException
import com.restaurant.user.application.port.input.RegisterAddressUseCase
import com.restaurant.user.domain.entity.Address
import com.restaurant.user.domain.exception.UserDomainException
import com.restaurant.user.domain.repository.UserRepository
import com.restaurant.user.domain.vo.AddressId
import com.restaurant.user.domain.vo.UserId
import mu.KotlinLogging
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

private val log = KotlinLogging.logger {}

@Service
class RegisterAddressCommandHandler(
    private val userRepository: UserRepository,
) : RegisterAddressUseCase {
    @Transactional
    override fun registerAddress(command: RegisterAddressCommand): AddressId {
        log.info { "Registering address for userId=${command.userId}, street=${command.street}, zipCode=${command.zipCode}" }

        try {
            val userIdVo = UserId.ofString(command.userId)
            val user = userRepository.findById(userIdVo) ?: throw UserDomainException.User.NotFound(userIdVo.toString())

            val addressId = AddressId.generate()
            val address =
                Address.create(
                    addressId = addressId,
                    street = command.street,
                    detail = command.detail,
                    zipCode = command.zipCode,
                    isDefault = command.isDefault,
                )
            val updatedUser = user.addAddress(address)
            userRepository.save(updatedUser)

            val addedAddressId = updatedUser.addresses.first { it.addressId == address.addressId }.addressId
            log.info { "Address registered successfully. userId=${userIdVo.value}, addressId=${addedAddressId.value}" }
            return addedAddressId
        } catch (de: UserDomainException) {
            log.warn(
                de,
            ) { "Domain error during address registration for userId=${command.userId}: code=${de.errorCode.code}, message=${de.message}" }
            throw de
        } catch (iae: IllegalArgumentException) {
            log.warn(iae) { "Invalid user ID format for address registration: ${command.userId}" }
            throw UserApplicationException.BadRequest("Invalid user ID format.", iae)
        } catch (e: Exception) {
            log.error(e) { "Unexpected error during address registration: userId=${command.userId}" }
            throw UserApplicationException.UnexpectedError("Failed to register address due to an unexpected error.", e)
        }
    }
}
