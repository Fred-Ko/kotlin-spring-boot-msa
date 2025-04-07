package com.restaurant.application.user.command.handler

import com.restaurant.application.user.command.RegisterUserCommand
import com.restaurant.application.user.common.UserErrorCode
import com.restaurant.common.core.command.CommandResult
import com.restaurant.domain.user.aggregate.User
import com.restaurant.domain.user.repository.UserRepository
import com.restaurant.domain.user.vo.Email
import com.restaurant.domain.user.vo.Name
import com.restaurant.domain.user.vo.Password
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import java.util.UUID

@Service
class RegisterUserCommandHandler(
    private val userRepository: UserRepository,
) {
    @Transactional
    fun handle(
        command: RegisterUserCommand,
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

        // 이메일 중복 검증
        if (userRepository.existsByEmail(email)) {
            return CommandResult.fail(
                correlationId = actualCorrelationId,
                errorCode = UserErrorCode.DUPLICATE_EMAIL.code,
                errorMessage = "이미 등록된 이메일입니다: ${command.email}",
            )
        }

        // 비밀번호 유효성 검증
        val password =
            try {
                Password.of(command.password)
            } catch (e: IllegalArgumentException) {
                return CommandResult.fail(
                    correlationId = actualCorrelationId,
                    errorCode = UserErrorCode.INVALID_INPUT.code,
                    errorMessage = "유효하지 않은 비밀번호 형식입니다.",
                )
            }

        // 이름 유효성 검증
        val name =
            try {
                Name.of(command.name)
            } catch (e: IllegalArgumentException) {
                return CommandResult.fail(
                    correlationId = actualCorrelationId,
                    errorCode = UserErrorCode.INVALID_INPUT.code,
                    errorMessage = "유효하지 않은 이름 형식입니다: ${command.name}",
                )
            }

        try {
            // 사용자 생성 및 저장
            val user = User.create(email, password, name)
            userRepository.save(user)

            // 성공 결과 반환
            return CommandResult.success(correlationId = actualCorrelationId)
        } catch (e: Exception) {
            // 시스템 오류 발생 시
            return CommandResult.fail(
                correlationId = actualCorrelationId,
                errorCode = UserErrorCode.SYSTEM_ERROR.code,
                errorMessage = "사용자 등록 중 시스템 오류가 발생했습니다.",
                errorDetails = mapOf("exception" to (e.message ?: "알 수 없는 오류")),
            )
        }
    }
}
