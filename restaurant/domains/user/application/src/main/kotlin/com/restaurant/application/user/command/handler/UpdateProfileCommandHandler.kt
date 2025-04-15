package com.restaurant.application.user.command.handler

import com.restaurant.application.user.command.UpdateProfileCommand
import com.restaurant.application.user.common.UserErrorCode
import com.restaurant.application.user.exception.UserApplicationException
import com.restaurant.domain.user.exception.UserDomainException
import com.restaurant.domain.user.repository.UserRepository
import com.restaurant.domain.user.vo.Name
import com.restaurant.domain.user.vo.UserId
import org.slf4j.LoggerFactory
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Service
class UpdateProfileCommandHandler(
    private val userRepository: UserRepository,
) {
    private val log = LoggerFactory.getLogger(UpdateProfileCommandHandler::class.java)

    @Transactional
    fun handle(
        command: UpdateProfileCommand,
        correlationId: String? = null,
    ) {
        try {
            // UserId 생성
            val userId =
                try {
                    UserId.of(command.userId)
                } catch (e: IllegalArgumentException) {
                    log.error("유효하지 않은 사용자 ID 형식, correlationId={}, userId={}", correlationId, command.userId, e)
                    throw UserApplicationException.Profile.InvalidInput("유효하지 않은 사용자 ID 형식입니다: ${command.userId}")
                }

            // 사용자 조회
            val user =
                userRepository.findById(userId)
                    ?: throw UserDomainException.User.NotFound(
                        userId = userId.toString(),
                        errorCode = UserErrorCode.NOT_FOUND,
                    )

            // 이름 생성
            val name =
                try {
                    Name.of(command.name)
                } catch (e: IllegalArgumentException) {
                    log.error("유효하지 않은 이름 형식, correlationId={}, name={}", correlationId, command.name, e)
                    throw UserApplicationException.Profile.InvalidInput("유효하지 않은 이름 형식입니다: ${command.name}")
                }

            // 프로필 업데이트
            val updatedUser = user.updateProfile(name = name)
            userRepository.save(updatedUser)

            log.info("사용자 프로필 업데이트 성공, correlationId={}, userId={}", correlationId, userId)
        } catch (e: UserDomainException.User.NotFound) {
            log.error(
                "사용자를 찾을 수 없음, correlationId={}, errorCode={}, error={}",
                correlationId,
                UserErrorCode.NOT_FOUND.code,
                e.message,
                e,
            )
            throw UserApplicationException.Query.NotFound(e.message ?: "사용자를 찾을 수 없습니다: ${command.userId}")
        } catch (e: UserApplicationException) {
            // 이미 애플리케이션 예외로 변환된 경우 그대로 전달
            throw e
        } catch (e: Exception) {
            log.error("사용자 프로필 업데이트 중 시스템 오류 발생, correlationId={}, error={}", correlationId, e.message, e)
            throw UserApplicationException.Profile.SystemError("사용자 프로필 업데이트 중 시스템 오류가 발생했습니다: ${e.message ?: "알 수 없는 오류"}")
        }
    }
}
