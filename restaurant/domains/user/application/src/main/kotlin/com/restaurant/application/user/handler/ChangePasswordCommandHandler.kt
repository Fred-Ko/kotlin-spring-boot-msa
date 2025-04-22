package com.restaurant.application.user.handler

import com.restaurant.application.user.command.ChangePasswordCommand
import com.restaurant.application.user.error.UserApplicationException
import com.restaurant.domain.user.error.UserDomainException
import com.restaurant.domain.user.repository.UserRepository
import com.restaurant.domain.user.vo.Password
import com.restaurant.domain.user.vo.UserId
import org.slf4j.LoggerFactory
import org.springframework.security.crypto.password.PasswordEncoder
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Service
class ChangePasswordCommandHandler(
    private val userRepository: UserRepository,
    private val passwordEncoder: PasswordEncoder,
) {
    private val log = LoggerFactory.getLogger(ChangePasswordCommandHandler::class.java)

    @Transactional
    fun handle(
        command: ChangePasswordCommand,
        correlationId: String? = null,
    ) {
        // Rule 14, 61: Create VOs outside try-catch
        val userId = UserId.fromString(command.userId)
        Password.validateRaw(command.newPassword)
        val encodedNewPassword = passwordEncoder.encode(command.newPassword)
        val newPasswordVo = Password.fromEncoded(encodedNewPassword)

        log.debug("Attempting password change, correlationId={}, userId={}", correlationId, userId)

        try {
            // 사용자 조회
            val user =
                userRepository.findById(userId)
                    ?: run {
                        log.warn("User not found for password change, correlationId={}, userId={}", correlationId, userId)
                        throw UserDomainException.User.NotFound(userId = command.userId)
                    }

            // 현재 비밀번호 검증 (Application Layer)
            if (!passwordEncoder.matches(command.currentPassword, user.password.encodedValue)) {
                log.warn("Password change failed: Current password mismatch, correlationId={}, userId={}", correlationId, userId)
                throw UserApplicationException.AuthenticationFailed()
            }

            // 비밀번호 변경 (Aggregate 호출)
            val updatedUser = user.changePassword(newPasswordVo)

            // 사용자 저장
            userRepository.save(updatedUser)

            log.info("Password changed successfully, correlationId={}, userId={}", correlationId, userId)
        } catch (de: UserDomainException) {
            // Rule 71: 로깅 시 errorCode 추가
            log.warn(
                "Domain error during password change, correlationId={}, userId={}, errorCode={}, error={}",
                correlationId,
                command.userId,
                de.errorCode.code,
                de.message,
            )
            throw de
        } catch (e: Exception) {
            log.error(
                "System error during password change, correlationId={}, userId={}, error={}",
                correlationId,
                command.userId,
                e.message,
                e,
            )
            throw UserApplicationException.SystemError(e)
        }
    }
}
