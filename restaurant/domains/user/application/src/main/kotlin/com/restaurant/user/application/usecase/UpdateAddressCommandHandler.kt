package com.restaurant.user.application.usecase

import com.restaurant.user.application.dto.command.UpdateAddressCommand
import com.restaurant.user.application.exception.UserApplicationException
import com.restaurant.user.application.port.input.UpdateAddressUseCase
import com.restaurant.user.domain.entity.Address
import com.restaurant.user.domain.exception.UserDomainException
import com.restaurant.user.domain.repository.UserRepository
import com.restaurant.user.domain.vo.AddressId
import com.restaurant.user.domain.vo.UserId
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Service
class UpdateAddressCommandHandler(
    private val userRepository: UserRepository,
) : UpdateAddressUseCase {
    @Transactional
    override fun updateAddress(command: UpdateAddressCommand) {
        try {
            val userIdVo = UserId.ofString(command.userId)
            val addressIdVo = AddressId.ofString(command.addressId)
            val user = userRepository.findById(userIdVo) ?: throw UserDomainException.User.NotFound(userIdVo.toString())

            val addressToUpdate =
                Address.create( // create로 새 주소 객체를 만들지만, ID는 기존 것을 사용합니다.
                    addressId = addressIdVo,
                    name = command.name,
                    streetAddress = command.street,
                    detailAddress = command.detail,
                    city = command.city,
                    state = command.state,
                    country = command.country,
                    zipCode = command.zipCode,
                    isDefault = command.isDefault,
                )

            val updatedUser = user.updateAddress(addressToUpdate)

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
