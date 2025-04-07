package com.restaurant.application.user.command.handler

import com.restaurant.application.user.command.DeleteUserCommand
import com.restaurant.application.user.common.UserErrorCode
import com.restaurant.common.core.command.CommandResult
import com.restaurant.domain.user.exception.InvalidCredentialsException
import com.restaurant.domain.user.exception.UserDomainException
import com.restaurant.domain.user.exception.UserNotFoundException
import com.restaurant.domain.user.repository.UserRepository
import com.restaurant.domain.user.vo.UserId
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import java.util.UUID

@Service
class DeleteUserCommandHandler(
    private val userRepository: UserRepository,
) {
    @Transactional
    fun handle(
        command: DeleteUserCommand,
        correlationId: String? = null,
    ): CommandResult {
        val actualCorrelationId = correlationId ?: UUID.randomUUID().toString()

        // UserId 생성
        val userId =
            try {
                UserId.of(command.userId)
            } catch (e: IllegalArgumentException) {
                return CommandResult.fail(
                    correlationId = actualCorrelationId,
                    errorCode = UserErrorCode.INVALID_INPUT.code,
                    errorMessage = "유효하지 않은 사용자 ID 형식입니다: ${command.userId}",
                )
            }

        try {
            // 사용자 조회
            val user =
                userRepository.findById(userId)
                    ?: throw UserNotFoundException(userId.toString())

            // 비밀번호 검증
            if (!user.checkPassword(command.password)) {
                throw InvalidCredentialsException()
            }

            // 사용자 삭제
            userRepository.delete(user)
            return CommandResult.success(correlationId = actualCorrelationId)
        } catch (e: UserNotFoundException) {
            // 사용자를 찾을 수 없는 경우
            return CommandResult.fail(
                correlationId = actualCorrelationId,
                errorCode = UserErrorCode.NOT_FOUND.code,
                errorMessage = e.message,
                errorDetails = mapOf("userId" to command.userId.toString()),
            )
        } catch (e: InvalidCredentialsException) {
            // 비밀번호 검증 실패
            return CommandResult.fail(
                correlationId = actualCorrelationId,
                errorCode = UserErrorCode.INVALID_CREDENTIALS.code,
                errorMessage = e.message,
                errorDetails = mapOf("userId" to command.userId.toString()),
            )
        } catch (e: UserDomainException) {
            // 기타 도메인 예외 처리
            return CommandResult.fail(
                correlationId = actualCorrelationId,
                errorCode = UserErrorCode.DELETION_FAILED.code,
                errorMessage = e.message,
                errorDetails = mapOf("exception" to e.javaClass.simpleName),
            )
        } catch (e: Exception) {
            // 기타 예외 처리
            return CommandResult.fail(
                correlationId = actualCorrelationId,
                errorCode = UserErrorCode.SYSTEM_ERROR.code,
                errorMessage = "사용자 삭제 중 시스템 오류가 발생했습니다.",
                errorDetails = mapOf("exception" to (e.message ?: "알 수 없는 오류")),
            )
        }
    }
}
