package com.restaurant.application.user.query.handler

import com.restaurant.application.user.common.UserErrorCode
import com.restaurant.application.user.exception.UserNotFoundApplicationException
import com.restaurant.application.user.query.GetUserProfileQuery
import com.restaurant.application.user.query.dto.UserProfileDto
import com.restaurant.domain.user.repository.UserRepository
import com.restaurant.domain.user.vo.UserId
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Service
class GetUserProfileQueryHandler(
  private val userRepository: UserRepository,
) {
  @Transactional(readOnly = true)
  fun handle(query: GetUserProfileQuery): UserProfileDto {
    try {
      val userId = UserId(query.userId)
      val user =
        userRepository.findById(userId)
          ?: throw UserNotFoundApplicationException(
            UserErrorCode.USER_NOT_FOUND.message,
          )

      return UserProfileDto(
        id = user.id!!.value,
        email = user.email.value,
        name = user.name.value,
        addresses = user.addresses,
        createdAt = user.createdAt,
        updatedAt = user.updatedAt,
      )
    } catch (e: UserNotFoundApplicationException) {
      throw e
    } catch (e: Exception) {
      throw RuntimeException(
        UserErrorCode.SYSTEM_ERROR.message,
        e,
      )
    }
  }
}
