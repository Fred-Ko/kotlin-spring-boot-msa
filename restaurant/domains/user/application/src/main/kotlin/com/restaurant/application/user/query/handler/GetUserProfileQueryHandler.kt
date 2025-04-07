package com.restaurant.application.user.query.handler

import com.restaurant.application.user.common.UserErrorCode
import com.restaurant.application.user.exception.UserApplicationException
import com.restaurant.application.user.exception.UserNotFoundApplicationException
import com.restaurant.application.user.extensions.toUserProfileDto
import com.restaurant.application.user.query.GetUserProfileQuery
import com.restaurant.application.user.query.dto.UserProfileDto
import com.restaurant.domain.user.exception.UserDomainException
import com.restaurant.domain.user.exception.UserNotFoundException
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
            val userId = UserId.of(query.userId)
            val user =
                userRepository.findById(userId)
                    ?: throw UserNotFoundException(userId.toString())

            return user.toUserProfileDto()
        } catch (e: IllegalArgumentException) {
            // 유효하지 않은 UserId 등의 문제
            throw UserNotFoundApplicationException(
                "유효하지 않은 사용자 ID 형식입니다: ${query.userId}",
            )
        } catch (e: UserNotFoundException) {
            // 도메인 예외를 애플리케이션 예외로 변환
            throw UserNotFoundApplicationException(
                UserErrorCode.NOT_FOUND.message,
            )
        } catch (e: UserDomainException) {
            // 기타 도메인 예외 처리
            throw UserApplicationException(e.message ?: "사용자 정보 조회 중 오류가 발생했습니다.")
        } catch (e: Exception) {
            // 기타 예외 처리
            throw UserApplicationException("사용자 프로필 조회 중 시스템 오류가 발생했습니다: ${e.message}")
        }
    }
}
