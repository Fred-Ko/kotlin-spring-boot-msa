package com.restaurant.user.application.command.handler

import com.restaurant.user.application.dto.command.DeleteAddressCommand
import com.restaurant.user.application.exception.UserApplicationException
import com.restaurant.user.application.command.usecase.DeleteAddressUseCase
import com.restaurant.user.domain.exception.UserDomainException
import com.restaurant.user.domain.repository.UserRepository
import com.restaurant.user.domain.vo.AddressId
import com.restaurant.user.domain.vo.UserId
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Service
class DeleteAddressCommandHandler(
    private val userRepository: UserRepository,
) : DeleteAddressUseCase {
    @Transactional
    override fun deleteAddress(command: DeleteAddressCommand) {
        try {
            val userIdVo = UserId.ofString(command.userId)
            val addressIdVo = AddressId.ofString(command.addressId)
            val user = userRepository.findById(userIdVo) ?: throw UserDomainException.User.NotFound(userIdVo.toString())

            val updatedUser = user.deleteAddress(addressIdVo)

            userRepository.save(updatedUser)
        } catch (de: UserDomainException) {
            throw de
        } catch (iae: IllegalArgumentException) {
            throw UserApplicationException.BadRequest("Invalid ID format.", iae)
        } catch (e: Exception) {
            throw UserApplicationException.UnexpectedError(message = "Failed to delete address due to an unexpected error.", cause = e)
        }
    }
}
