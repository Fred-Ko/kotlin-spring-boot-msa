package com.restaurant.application.user.handler

import com.restaurant.application.user.command.DeleteUserCommand
import com.restaurant.application.user.error.UserApplicationErrorCode
import com.restaurant.application.user.error.UserApplicationException
import com.restaurant.domain.user.error.UserDomainException
import com.restaurant.domain.user.repository.UserRepository
import com.restaurant.domain.user.vo.UserId
import org.slf4j.LoggerFactory
import org.springframework.security.crypto.password.PasswordEncoder
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Service
class DeleteUserCommandHandler(
    private val userRepository: UserRepository,
    private val passwordEncoder: PasswordEncoder,
) {
    private val log = LoggerFactory.getLogger(DeleteUserCommandHandler::class.java)

    @Transactional
    fun handle(
        command: DeleteUserCommand,
        correlationId: String? = null,
    ) {
        // VO 생성
        val userId = UserId.fromString(command.userId)
        log.debug("Attempting user deletion, correlationId={}, userId={}", correlationId, userId)

        try {
            // 사용자 조회
            val user =
                userRepository.findById(userId)
                    ?: run {
                        log.warn("User not found for deletion, correlationId={}, userId={}", correlationId, userId)
                        throw UserDomainException.User.NotFound(userId = command.userId)
                    }

            // 비밀번호 검증 (Application Layer)
            if (!passwordEncoder.matches(command.password, user.password.encodedValue)) {
                log.warn("Password mismatch during user deletion, correlationId={}, userId={}", correlationId, userId)
                throw UserApplicationException.AuthenticationFailed()
            }

            // 사용자 삭제
            userRepository.delete(user)

            log.info("User deleted successfully, correlationId={}, userId={}", correlationId, userId)
        } catch (de: UserDomainException) {
            // Rule 71: 로깅 시 errorCode 추가
            log.warn(
                "Domain error during user deletion, correlationId={}, userId={}, errorCode={}, error: {}",
                correlationId,
                command.userId,
                de.errorCode.code,
                de.message,
            )
            throw de
        } catch (e: Exception) {
            // 시스템 오류
            log.error(
                "System error during user deletion, correlationId={}, userId={}, errorCode={}, error={}",
                correlationId,
                command.userId,
                UserApplicationErrorCode.SYSTEM_ERROR.code,
                e.message,
                e,
            )
            throw UserApplicationException.SystemError(e)
        }
    }
}
