package com.restaurant.application.user.command.handler

import com.restaurant.application.user.command.DeleteUserCommand
import com.restaurant.application.user.common.UserErrorCode
import com.restaurant.application.user.exception.UserApplicationException
import com.restaurant.domain.user.exception.UserDomainException
import com.restaurant.domain.user.repository.UserRepository
import com.restaurant.domain.user.vo.UserId
import org.slf4j.LoggerFactory
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Service
class DeleteUserCommandHandler(
    private val userRepository: UserRepository,
) {
    private val log = LoggerFactory.getLogger(DeleteUserCommandHandler::class.java)

    @Transactional
    fun handle(
        command: DeleteUserCommand,
        correlationId: String? = null,
    ) {
        try {
            // UserId 생성
            val userId =
                try {
                    UserId.of(command.userId)
                } catch (e: IllegalArgumentException) {
                    log.error("유효하지 않은 사용자 ID 형식, correlationId={}, userId={}", correlationId, command.userId, e)
                    throw UserApplicationException.Deletion.InvalidInput("유효하지 않은 사용자 ID 형식입니다: ${command.userId}")
                }

            // 사용자 조회
            val user =
                userRepository.findById(userId)
                    ?: throw UserDomainException.User.NotFound(
                        userId = userId.toString(),
                        errorCode = UserErrorCode.NOT_FOUND,
                    )

            // 비밀번호 검증
            if (!user.password.matches(command.password)) {
                throw UserDomainException.User.InvalidCredentials(
                    errorCode = UserErrorCode.INVALID_CREDENTIALS,
                    message = "비밀번호가 일치하지 않습니다.",
                )
            }

            // 사용자 삭제
            userRepository.delete(user)

            log.info("사용자 삭제 성공, correlationId={}, userId={}", correlationId, userId)
        } catch (e: UserDomainException.User.NotFound) {
            // 사용자를 찾을 수 없는 경우
            log.error(
                "사용자를 찾을 수 없음, correlationId={}, errorCode={}, error={}",
                correlationId,
                UserErrorCode.NOT_FOUND.code,
                e.message,
                e,
            )
            throw UserApplicationException.Query.NotFound(e.message ?: "사용자를 찾을 수 없습니다: ${command.userId}")
        } catch (e: UserDomainException.User.InvalidCredentials) {
            // 비밀번호 검증 실패 시
            log.error(
                "비밀번호 검증 실패, correlationId={}, errorCode={}, error={}",
                correlationId,
                UserErrorCode.INVALID_CREDENTIALS.code,
                e.message,
                e,
            )
            throw UserApplicationException.Password.InvalidPassword(e.message ?: "비밀번호가 일치하지 않습니다.")
        } catch (e: UserApplicationException) {
            // 이미 애플리케이션 예외로 변환된 경우 그대로 전달
            throw e
        } catch (e: Exception) {
            // 기타 예외 처리
            log.error("사용자 삭제 중 시스템 오류 발생, correlationId={}, error={}", correlationId, e.message, e)
            throw UserApplicationException.Deletion.SystemError("사용자 삭제 중 시스템 오류가 발생했습니다: ${e.message ?: "알 수 없는 오류"}")
        }
    }
}
