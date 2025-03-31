package com.restaurant.application.user.command.handler

import com.restaurant.application.user.command.RegisterAddressCommand
import com.restaurant.application.user.common.UserErrorCode
import com.restaurant.common.core.command.CommandResult
import com.restaurant.domain.user.repository.UserRepository
import com.restaurant.domain.user.vo.Address
import com.restaurant.domain.user.vo.UserId
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import java.util.UUID

@Service
class RegisterAddressCommandHandler(
  private val userRepository: UserRepository,
) {
  @Transactional
  fun handle(command: RegisterAddressCommand): CommandResult {
    try {
      val userId = UserId(command.userId)
      val user =
        userRepository.findById(userId)
          ?: return CommandResult(false, errorCode = UserErrorCode.USER_NOT_FOUND.code)

      val address =
        Address.create(
          street = command.street,
          detail = command.detail,
          zipCode = command.zipCode,
          isDefault = command.isDefault,
        )

      val updatedUser = user.addAddress(address)
      userRepository.save(updatedUser)

      return CommandResult(true, UUID.randomUUID().toString())
    } catch (e: IllegalArgumentException) {
      return CommandResult(false, errorCode = UserErrorCode.INVALID_INPUT.code)
    } catch (e: Exception) {
      return CommandResult(false, errorCode = UserErrorCode.SYSTEM_ERROR.code)
    }
  }
}
