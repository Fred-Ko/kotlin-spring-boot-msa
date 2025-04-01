package com.restaurant.application.user.command.handler

import com.restaurant.application.user.command.DeleteAddressCommand
import com.restaurant.application.user.common.UserErrorCode
import com.restaurant.common.core.command.CommandResult
import com.restaurant.domain.user.repository.UserRepository
import com.restaurant.domain.user.vo.UserId
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import java.util.UUID

@Service
class DeleteAddressCommandHandler(
    private val userRepository: UserRepository,
) {
    @Transactional
    fun handle(command: DeleteAddressCommand): CommandResult {
        try {
            val userId = UserId(command.userId)
            val user =
                userRepository.findById(userId)
                    ?: return CommandResult(false, errorCode = UserErrorCode.NOT_FOUND.code)

            // 삭제할 주소가 존재하는지 확인
            val existingAddress =
                user.addresses.find { it.id == command.addressId }
                    ?: return CommandResult(
                        false,
                        errorCode = UserErrorCode.ADDRESS_NOT_FOUND.code,
                    )

            // 주소 삭제
            val updatedUser = user.removeAddress(command.addressId)
            userRepository.save(updatedUser)

            return CommandResult(true, UUID.randomUUID().toString())
        } catch (e: IllegalArgumentException) {
            return CommandResult(false, errorCode = UserErrorCode.INVALID_INPUT.code)
        } catch (e: Exception) {
            return CommandResult(false, errorCode = UserErrorCode.SYSTEM_ERROR.code)
        }
    }
}
