package com.restaurant.application.user.command.handler

import com.restaurant.application.user.command.RegisterAddressCommand
import com.restaurant.application.user.common.UserErrorCode
import com.restaurant.application.user.exception.UserApplicationException
import com.restaurant.domain.user.entity.Address
import com.restaurant.domain.user.exception.UserDomainException
import com.restaurant.domain.user.repository.UserRepository
import com.restaurant.domain.user.vo.UserId
import org.slf4j.LoggerFactory
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Service
class RegisterAddressCommandHandler(
    private val userRepository: UserRepository,
) {
    private val log = LoggerFactory.getLogger(RegisterAddressCommandHandler::class.java)

    @Transactional
    fun handle(
        command: RegisterAddressCommand,
        correlationId: String? = null,
    ) {
        try {
            // UserId 생성
            val userId =
                try {
                    UserId.of(command.userId)
                } catch (e: IllegalArgumentException) {
                    log.error("유효하지 않은 사용자 ID 형식, correlationId={}, userId={}", correlationId, command.userId, e)
                    throw UserApplicationException.Address.InvalidInput("유효하지 않은 사용자 ID 형식입니다: ${command.userId}")
                }

            // 사용자 조회
            val user =
                userRepository.findById(userId)
                    ?: throw UserDomainException.User.NotFound(
                        userId = userId.toString(),
                        errorCode = UserErrorCode.NOT_FOUND,
                    )

            // 주소 생성
            val address =
                try {
                    Address.create(
                        street = command.street,
                        detail = command.detail,
                        zipCode = command.zipCode,
                        isDefault = command.isDefault,
                    )
                } catch (e: IllegalArgumentException) {
                    log.error("유효하지 않은 주소 정보, correlationId={}, error={}", correlationId, e.message, e)
                    throw UserApplicationException.Address.InvalidInput("유효하지 않은 주소 정보입니다: ${e.message}")
                }

            // 주소 추가
            val updatedUser = user.addAddress(address)
            userRepository.save(updatedUser)

            log.info("주소 등록 성공, correlationId={}, userId={}", correlationId, userId)
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
        } catch (e: UserDomainException.Address.MaxLimitExceeded) {
            // 최대 주소 개수 초과
            log.error(
                "최대 주소 개수 초과, correlationId={}, errorCode={}, error={}",
                correlationId,
                UserErrorCode.MAX_ADDRESS_LIMIT.code,
                e.message,
                e,
            )
            throw UserApplicationException.Address.MaxLimitExceeded(e.message ?: "최대 주소 등록 개수를 초과했습니다.")
        } catch (e: UserDomainException) {
            // 기타 도메인 예외 처리
            log.error(
                "도메인 예외 발생, correlationId={}, errorCode={}, error={}",
                correlationId,
                UserErrorCode.SYSTEM_ERROR.code,
                e.message,
                e,
            )
            throw UserApplicationException.Address.SystemError(e.message ?: "주소 등록 중 오류가 발생했습니다.")
        } catch (e: UserApplicationException) {
            // 이미 애플리케이션 예외로 변환된 경우 그대로 전달
            throw e
        } catch (e: Exception) {
            // 기타 예외 처리
            log.error("주소 등록 중 시스템 오류 발생, correlationId={}, error={}", correlationId, e.message, e)
            throw UserApplicationException.Address.SystemError("주소 등록 중 시스템 오류가 발생했습니다: ${e.message ?: "알 수 없는 오류"}")
        }
    }
}
