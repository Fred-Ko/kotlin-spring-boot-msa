package com.restaurant.user.application.command.handler

import com.restaurant.user.application.command.dto.RegisterAddressCommand
import com.restaurant.user.application.command.usecase.RegisterAddressUseCase
import com.restaurant.user.application.exception.UserApplicationException
import com.restaurant.user.domain.entity.Address
import com.restaurant.user.domain.exception.UserDomainException
import com.restaurant.user.domain.repository.UserRepository
import com.restaurant.user.domain.vo.AddressId
import com.restaurant.user.domain.vo.UserId
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Service
class RegisterAddressCommandHandler(
    private val userRepository: UserRepository,
) : RegisterAddressUseCase {
    @Transactional
    override fun registerAddress(command: RegisterAddressCommand): AddressId {
        try {
            val userIdVo = UserId.ofString(command.userId)
            val user = userRepository.findById(userIdVo) ?: throw UserDomainException.User.NotFound(userIdVo.toString())

            val addressId = AddressId.generate()
            val address =
                Address.create(
                    addressId = addressId,
                    name = command.name,
                    streetAddress = command.street,
                    detailAddress = command.detail,
                    city = command.city,
                    state = command.state,
                    country = command.country,
                    zipCode = command.zipCode,
                    isDefault = command.isDefault,
                )
            val updatedUser = user.addAddress(address)
            userRepository.save(updatedUser)

            return updatedUser.addresses.first { it.addressId == address.addressId }.addressId
        } catch (iae: IllegalArgumentException) {
            throw UserApplicationException.BadRequest("Invalid user ID format.", iae)
        } catch (e: Exception) {
            throw UserApplicationException.UnexpectedError(message = "Failed to register address due to an unexpected error.", cause = e)
        }
    }
}
