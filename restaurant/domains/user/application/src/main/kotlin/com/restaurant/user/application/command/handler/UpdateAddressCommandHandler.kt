package com.restaurant.user.application.command.handler

import com.restaurant.user.application.command.IUpdateAddressCommandHandler
import com.restaurant.user.application.command.dto.UpdateAddressCommand
import com.restaurant.user.application.exception.UserApplicationException
import com.restaurant.user.domain.exception.UserDomainException
import com.restaurant.user.domain.repository.UserRepository
import com.restaurant.user.domain.vo.AddressId
import com.restaurant.user.domain.vo.Name
import com.restaurant.user.domain.vo.UserId
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Service
class UpdateAddressCommandHandler(
    private val userRepository: UserRepository,
) : IUpdateAddressCommandHandler {
    @Transactional
    override fun updateAddress(command: UpdateAddressCommand) {
        try {
            val userIdVo = UserId.ofString(command.userId)
            val addressIdVo = AddressId.ofString(command.addressId)
            val user = userRepository.findById(userIdVo) ?: throw UserDomainException.User.NotFound(userIdVo.toString())

            val updatedUser =
                user.updateAddress(
                    addressId = addressIdVo,
                    name = Name.of(command.name),
                    streetAddress = command.street,
                    detailAddress = command.detail,
                    city = command.city,
                    state = command.state,
                    country = command.country,
                    zipCode = command.zipCode,
                    isDefault = command.isDefault,
                )

            userRepository.save(updatedUser)
        } catch (de: UserDomainException) {
            throw de
        } catch (iae: IllegalArgumentException) {
            throw UserApplicationException.BadRequest("Invalid ID format.", iae)
        } catch (e: Exception) {
            throw UserApplicationException.UnexpectedError(message = "Failed to update address due to an unexpected error.", cause = e)
        }
    }
}
