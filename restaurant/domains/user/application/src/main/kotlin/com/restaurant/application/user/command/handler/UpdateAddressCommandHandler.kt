package com.restaurant.application.user.command.handler

import com.restaurant.application.user.command.UpdateAddressCommand
import com.restaurant.application.user.common.UserErrorCode
import com.restaurant.common.core.command.CommandResult
import com.restaurant.domain.user.entity.Address
import com.restaurant.domain.user.repository.UserRepository
import com.restaurant.domain.user.vo.UserId
import java.util.UUID
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Service
class UpdateAddressCommandHandler(
        private val userRepository: UserRepository,
) {
        @Transactional
        fun handle(command: UpdateAddressCommand): CommandResult {
                try {
                        val userId = UserId(command.userId)
                        val user =
                                userRepository.findById(userId)
                                        ?: return CommandResult(
                                                false,
                                                errorCode = UserErrorCode.NOT_FOUND.code
                                        )

                        // 업데이트할 주소가 존재하는지 확인
                        val existingAddress =
                                user.addresses.find { it.id == command.addressId }
                                        ?: return CommandResult(
                                                false,
                                                errorCode = UserErrorCode.ADDRESS_NOT_FOUND.code
                                        )

                        // 새 주소 객체 생성
                        val updatedAddress =
                                Address.reconstitute(
                                        id = command.addressId,
                                        street = command.street,
                                        detail = command.detail,
                                        zipCode = command.zipCode,
                                        isDefault = command.isDefault,
                                )

                        // 주소 업데이트
                        val updatedUser = user.updateAddress(command.addressId, updatedAddress)
                        userRepository.save(updatedUser)

                        return CommandResult(true, UUID.randomUUID().toString())
                } catch (e: IllegalArgumentException) {
                        return CommandResult(false, errorCode = UserErrorCode.INVALID_INPUT.code)
                } catch (e: Exception) {
                        return CommandResult(false, errorCode = UserErrorCode.SYSTEM_ERROR.code)
                }
        }
}
