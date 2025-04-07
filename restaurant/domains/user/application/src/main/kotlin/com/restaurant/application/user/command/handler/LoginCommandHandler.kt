package com.restaurant.application.user.command.handler

import com.restaurant.application.user.command.LoginCommand
import com.restaurant.application.user.common.UserErrorCode
import com.restaurant.common.core.command.CommandResult
import com.restaurant.domain.user.repository.UserRepository
import com.restaurant.domain.user.vo.Email
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import java.util.UUID

@Service
class LoginCommandHandler(
    private val userRepository: UserRepository,
) {
    @Transactional(readOnly = true)
    fun handle(
        command: LoginCommand,
        correlationId: String? = null,
    ): CommandResult {
        val actualCorrelationId = correlationId ?: UUID.randomUUID().toString()

        // 이메일 유효성 검증
        val email =
            try {
                Email.of(command.email)
            } catch (e: IllegalArgumentException) {
                return CommandResult.fail(
                    correlationId = actualCorrelationId,
                    errorCode = UserErrorCode.INVALID_INPUT.code,
                    errorMessage = "유효하지 않은 이메일 형식입니다: ${command.email}",
                )
            }

        // 사용자 조회
        val user =
            userRepository.findByEmail(email)
                ?: return CommandResult.fail(
                    correlationId = actualCorrelationId,
                    errorCode = UserErrorCode.INVALID_CREDENTIALS.code,
                    errorMessage = "이메일 또는 비밀번호가 올바르지 않습니다.",
                )

        // 비밀번호 검증
        if (!user.checkPassword(command.password)) {
            return CommandResult.fail(
                correlationId = actualCorrelationId,
                errorCode = UserErrorCode.INVALID_CREDENTIALS.code,
                errorMessage = "이메일 또는 비밀번호가 올바르지 않습니다.",
            )
        }

        // 로그인 성공 - 실제로는 여기서 세션이나 JWT 토큰을 생성해야 함
        return CommandResult.success(correlationId = actualCorrelationId)
    }
}
