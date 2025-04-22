package com.restaurant.application.user.command.handler

import com.restaurant.application.user.command.UpdateAddressCommand
import com.restaurant.domain.user.error.UserDomainException
import com.restaurant.domain.user.model.Address
import com.restaurant.domain.user.repository.UserRepository
import com.restaurant.domain.user.vo.AddressId
import com.restaurant.domain.user.vo.UserId
import org.slf4j.LoggerFactory
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Service
public class UpdateAddressCommandHandler(
    private val userRepository: UserRepository,
) {
    private val log = LoggerFactory.getLogger(UpdateAddressCommandHandler::class.java)

    @Transactional
    fun handle(
        command: UpdateAddressCommand,
        correlationId: String? = null,
    ) {
        val userId = UserId.fromString(command.userId)
        val addressId = AddressId.fromString(command.addressId)
        val updatedAddressData =
            Address.reconstitute(
                addressId = addressId,
                street = command.street,
                detail = command.detail,
                zipCode = command.zipCode,
                isDefault = command.isDefault,
            )
        log.debug("Attempting to update address, correlationId={}, userId={}, addressId={}", correlationId, userId, addressId)
        val user =
            userRepository.findById(userId)
                ?: run {
                    log.warn("User not found for address update, correlationId={}, userId={}", correlationId, userId)
                    throw UserDomainException.User.NotFound(userId = command.userId)
                }
        val updatedUser = user.updateAddress(addressId, updatedAddressData)
        userRepository.save(updatedUser)
        log.info("Address updated successfully, correlationId={}, userId={}, addressId={}", correlationId, userId, addressId)
    }
}
