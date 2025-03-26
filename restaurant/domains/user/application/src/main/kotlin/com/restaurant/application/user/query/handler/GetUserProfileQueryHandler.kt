package com.restaurant.application.user.query.handler

import com.restaurant.application.user.common.UserErrorCode
import com.restaurant.application.user.query.GetUserProfileQuery
import com.restaurant.application.user.query.dto.UserProfileDto
import com.restaurant.common.core.query.QueryResult
import com.restaurant.domain.user.repository.UserRepository
import com.restaurant.domain.user.vo.UserId
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Service
class GetUserProfileQueryHandler(
  private val userRepository: UserRepository,
) {
  @Transactional(readOnly = true)
  fun handle(query: GetUserProfileQuery): QueryResult<UserProfileDto> {
    try {
      val userId = UserId(query.userId)
      val user =
        userRepository.findById(userId)
          ?: return QueryResult(
            success = false,
            errorCode = UserErrorCode.NOT_FOUND.code,
          )

      return QueryResult(
        success = true,
        data =
          UserProfileDto(
            id = user.id?.value ?: 0,
            email = user.email.value,
            name = user.name,
            createdAt = user.createdAt,
            updatedAt = user.updatedAt,
          ),
      )
    } catch (e: IllegalArgumentException) {
      return QueryResult(success = false, errorCode = UserErrorCode.INVALID_INPUT.code)
    } catch (e: Exception) {
      return QueryResult(success = false, errorCode = UserErrorCode.SYSTEM_ERROR.code)
    }
  }
}
