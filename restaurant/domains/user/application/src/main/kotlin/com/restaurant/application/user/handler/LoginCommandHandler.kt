package com.restaurant.application.user.handler

import com.restaurant.application.user.command.LoginCommand
import com.restaurant.application.user.exception.UserApplicationException
import com.restaurant.domain.user.repository.UserRepository
import com.restaurant.domain.user.vo.Email
import org.slf4j.LoggerFactory
import org.springframework.security.crypto.password.PasswordEncoder
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Service
class LoginCommandHandler(
    private val userRepository: UserRepository,
    private val passwordEncoder: PasswordEncoder,
) {
    private val log = LoggerFactory.getLogger(LoginCommandHandler::class.java)

    @Transactional(readOnly = true)
    fun handle(
        command: LoginCommand,
        correlationId: String? = null,
    ): String {
        // VO 생성
        val email = Email.of(command.email)
        log.debug("Attempting to login user, correlationId={}, email={}", correlationId, email)

        try {
            // 사용자 조회
            val user =
                userRepository.findByEmail(email)
                    ?: run {
                        log.warn("User not found for login, correlationId={}, email={}", correlationId, email)
                        // 사용자가 없는 경우에도 동일한 인증 실패 메시지를 반환하여 정보 노출 방지
                        throw UserApplicationException.AuthenticationFailed("이메일 또는 비밀번호가 일치하지 않습니다.")
                    }

            // 비밀번호 검증
            if (!passwordEncoder.matches(command.password, user.password.encodedValue)) {
                log.warn("Invalid password for login, correlationId={}, email={}", correlationId, email)
                throw UserApplicationException.AuthenticationFailed("이메일 또는 비밀번호가 일치하지 않습니다.")
            }

            // 로그인 성공 처리 (UserId 반환)
            log.info("User logged in successfully, correlationId={}, userId={}", correlationId, user.id.value)
            return user.id.value.toString()
        } catch (e: Exception) {
            when (e) {
                is UserApplicationException -> {
                    // Rule 71: 로깅 시 errorCode 추가
                    log.warn(
                        "Application error during login, correlationId={}, email={}, errorCode={}, error: {}",
                        correlationId,
                        command.email,
                        e.errorCode.code,
                        e.message,
                    )
                    throw e
                }
                else -> {
                    // 예상치 못한 오류 처리
                    log.error("System error during login, correlationId={}, email={}, error={}", correlationId, command.email, e.message, e)
                    throw UserApplicationException.SystemError(e)
                }
            }
        }
    }
}
