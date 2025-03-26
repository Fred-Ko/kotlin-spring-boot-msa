package com.restaurant.application.user.command.handler

import com.restaurant.application.user.command.UpdateProfileCommand
import com.restaurant.application.user.exception.UserProfileUpdateException
import com.restaurant.common.core.command.CommandResult
import com.restaurant.domain.user.exception.UserNotFoundException
import com.restaurant.domain.user.repository.UserRepository
import com.restaurant.domain.user.vo.UserId
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import java.util.UUID

@Service
class UpdateProfileCommandHandler(
  private val userRepository: UserRepository,
) {
  @Transactional
  fun handle(command: UpdateProfileCommand): CommandResult {
    try {
      val userId = UserId(command.userId)
      val user =
        userRepository.findById(userId)
          ?: throw UserNotFoundException(userId.toString())

      val updatedUser = user.updateProfile(name = command.name)

      userRepository.save(updatedUser)

      return CommandResult(true, UUID.randomUUID().toString())
    } catch (e: UserNotFoundException) {
      throw UserProfileUpdateException("사용자를 찾을 수 없습니다: ${command.userId}")
    } catch (e: IllegalArgumentException) {
      throw UserProfileUpdateException(e.message ?: "유효하지 않은 입력값입니다.")
    } catch (e: Exception) {
      throw UserProfileUpdateException("프로필 업데이트 중 오류가 발생했습니다: ${e.message}")
    }
  }
}
