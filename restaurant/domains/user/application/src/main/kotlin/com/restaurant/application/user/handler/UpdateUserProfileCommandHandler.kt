package com.restaurant.application.user.handler

import com.restaurant.application.user.command.UpdateProfileCommand
import com.restaurant.application.user.error.UserApplicationException
import com.restaurant.domain.user.error.UserDomainException
import com.restaurant.domain.user.repository.UserRepository
import com.restaurant.domain.user.vo.Name
import com.restaurant.domain.user.vo.UserId
import org.slf4j.LoggerFactory
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

/**
 * 사용자 프로필 업데이트 커맨드 핸들러
 */
@Service
class UpdateUserProfileCommandHandler(
    private val userRepository: UserRepository,
) {
    private val log = LoggerFactory.getLogger(UpdateUserProfileCommandHandler::class.java)

    @Transactional
    fun handle(
        command: UpdateProfileCommand,
        correlationId: String? = null,
    ) {
        // Rule 14, 61: VO 생성
        val userId = UserId.fromString(command.userId)
        val name = Name.of(command.name)

        log.debug(
            "Attempting to update user profile, correlationId={}, userId={}",
            correlationId,
            userId,
        )

        try {
            // 사용자 조회
            val user =
                userRepository.findById(userId) ?: run {
                    log.warn(
                        "User not found for profile update, correlationId={}, userId={}",
                        correlationId,
                        userId,
                    )
                    throw UserDomainException.User.NotFound(userId = command.userId)
                }

            // 프로필 업데이트 (Domain 로직 호출)
            val updatedUser = user.updateProfile(name)

            // 사용자 저장
            userRepository.save(updatedUser)

            log.info(
                "User profile updated successfully, correlationId={}, userId={}",
                correlationId,
                userId,
            )
        } catch (de: UserDomainException) {
            // Rule 71: 로깅 시 errorCode 추가
            log.warn(
                "Domain error during profile update, correlationId={}, userId={}, errorCode={}, error={}",
                correlationId,
                userId,
                de.errorCode.code,
                de.message,
            )
            throw de
        } catch (e: Exception) {
            // 예상치 못한 시스템 오류
            log.error(
                "System error during profile update, correlationId={}, userId={}, error={}",
                correlationId,
                userId,
                e.message,
                e,
            )
            throw UserApplicationException.SystemError(e)
        }
    }
}
