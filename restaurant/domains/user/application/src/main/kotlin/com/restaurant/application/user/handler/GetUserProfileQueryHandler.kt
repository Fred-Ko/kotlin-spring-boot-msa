package com.restaurant.application.user.handler

import com.restaurant.application.user.dto.UserProfileDto
import com.restaurant.application.user.extensions.toUserProfileDto
import com.restaurant.application.user.query.GetUserProfileQuery
import com.restaurant.domain.user.error.UserDomainException
import com.restaurant.domain.user.repository.UserRepository
import com.restaurant.domain.user.vo.UserId
import org.slf4j.LoggerFactory
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Service
class GetUserProfileQueryHandler(
    private val userRepository: UserRepository,
) {
    private val log = LoggerFactory.getLogger(GetUserProfileQueryHandler::class.java)

    @Transactional(readOnly = true)
    fun handle(
        query: GetUserProfileQuery,
        correlationId: String? = null,
    ): UserProfileDto {
        val userId = UserId.fromString(query.userId)
        log.debug("Attempting to get user profile, correlationId={}, userId={}", correlationId, userId)
        val user =
            userRepository.findById(userId)
                ?: run {
                    log.warn("User not found for profile query, correlationId={}, userId={}", correlationId, userId)
                    throw UserDomainException.User.NotFound(userId = query.userId)
                }
        val result = user.toUserProfileDto()
        log.debug("User profile retrieved successfully, correlationId={}, userId={}", correlationId, userId)
        return result
    }
}
