package com.restaurant.application.user.query.handler

import com.restaurant.application.user.common.UserErrorCode
import com.restaurant.application.user.exception.UserApplicationException
import com.restaurant.application.user.extensions.toUserProfileDto
import com.restaurant.application.user.query.GetUserProfileQuery
import com.restaurant.application.user.query.dto.UserProfileDto
import com.restaurant.domain.user.exception.UserDomainException
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
        try {
            val userId =
                try {
                    UserId.of(query.userId)
                } catch (e: IllegalArgumentException) {
                    log.error("유효하지 않은 사용자 ID 형식, correlationId={}, userId={}", correlationId, query.userId, e)
                    throw UserApplicationException.Query.InvalidInput("유효하지 않은 사용자 ID 형식입니다: ${query.userId}")
                }

            val user =
                userRepository.findById(userId)
                    ?: throw UserDomainException.User.NotFound(
                        userId = userId.toString(),
                        errorCode = UserErrorCode.NOT_FOUND,
                    )

            return user.toUserProfileDto()
        } catch (e: UserDomainException.User.NotFound) {
            // 사용자를 찾을 수 없는 경우
            log.error(
                "사용자를 찾을 수 없음, correlationId={}, errorCode={}, error={}",
                correlationId,
                UserErrorCode.NOT_FOUND.code,
                e.message,
                e,
            )
            throw UserApplicationException.Query.NotFound(e.message ?: "사용자를 찾을 수 없습니다: ${query.userId}")
        } catch (e: UserApplicationException) {
            // 이미 애플리케이션 예외로 변환된 경우 그대로 전달
            throw e
        } catch (e: Exception) {
            // 기타 예외 처리
            log.error("사용자 프로필 조회 중 시스템 오류 발생, correlationId={}, error={}", correlationId, e.message, e)
            throw UserApplicationException.Query.SystemError("사용자 프로필 조회 중 시스템 오류가 발생했습니다: ${e.message ?: "알 수 없는 오류"}")
        }
    }
}
