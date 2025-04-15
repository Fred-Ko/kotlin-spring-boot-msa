package com.restaurant.application.user.command.handler

import com.restaurant.application.user.command.LoginCommand
import com.restaurant.application.user.exception.UserApplicationException
import com.restaurant.domain.user.repository.UserRepository
import com.restaurant.domain.user.vo.Email
import org.slf4j.LoggerFactory
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Service
class LoginCommandHandler(
    private val userRepository: UserRepository,
) {
    private val log = LoggerFactory.getLogger(LoginCommandHandler::class.java)

    @Transactional(readOnly = true)
    fun handle(
        command: LoginCommand,
        correlationId: String? = null,
    ): Long {
        // 이메일 유효성 검증
        val email =
            try {
                Email.of(command.email)
            } catch (e: IllegalArgumentException) {
                log.error("유효하지 않은 이메일 형식, correlationId={}, email={}", correlationId, command.email, e)
                throw UserApplicationException.Authentication.InvalidInput("유효하지 않은 이메일 형식입니다: ${command.email}")
            }

        // 사용자 조회
        val user =
            userRepository.findByEmail(email)
                ?: run {
                    log.error("이메일에 해당하는 사용자 없음, correlationId={}, email={}", correlationId, email)
                    throw UserApplicationException.Authentication.InvalidCredentials()
                }

        // 비밀번호 검증
        if (!user.checkPassword(command.password)) {
            log.error("비밀번호 불일치, correlationId={}, email={}", correlationId, email)
            throw UserApplicationException.Authentication.InvalidCredentials()
        }

        // 로그인 성공 - 실제로는 여기서 세션이나 JWT 토큰을 생성해야 함
        log.info("로그인 성공, correlationId={}, email={}", correlationId, email)

        return user.id!!.value
    }
}
