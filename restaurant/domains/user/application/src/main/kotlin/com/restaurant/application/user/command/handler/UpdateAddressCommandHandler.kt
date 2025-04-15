package com.restaurant.application.user.command.handler

import com.restaurant.application.user.command.UpdateAddressCommand
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
class UpdateAddressCommandHandler(
    private val userRepository: UserRepository,
) {
    private val log = LoggerFactory.getLogger(UpdateAddressCommandHandler::class.java)

    @Transactional
    fun handle(
        command: UpdateAddressCommand,
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

            // 변경할 주소가 존재하는지 확인
            val existingAddress =
                user.addresses.find { it.id == command.addressId }
                    ?: throw UserDomainException.Address.NotFound(
                        userId = command.userId,
                        addressId = command.addressId,
                        errorCode = UserErrorCode.ADDRESS_NOT_FOUND,
                    )

            // 주소 정보 업데이트
            val updatedAddress =
                try {
                    Address.reconstitute(
                        id = command.addressId,
                        street = command.street,
                        detail = command.detail,
                        zipCode = command.zipCode,
                        isDefault = command.isDefault,
                    )
                } catch (e: IllegalArgumentException) {
                    log.error("유효하지 않은 주소 정보, correlationId={}, error={}", correlationId, e.message, e)
                    throw UserApplicationException.Address.InvalidInput("유효하지 않은 주소 정보입니다: ${e.message}")
                }

            // 주소 업데이트
            val updatedUser = user.updateAddress(command.addressId, updatedAddress)
            userRepository.save(updatedUser)

            log.info("주소 업데이트 성공, correlationId={}, userId={}, addressId={}", correlationId, userId, command.addressId)
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
        } catch (e: UserDomainException.Address.NotFound) {
            // 주소를 찾을 수 없는 경우
            log.error(
                "주소를 찾을 수 없음, correlationId={}, errorCode={}, error={}",
                correlationId,
                UserErrorCode.ADDRESS_NOT_FOUND.code,
                e.message,
                e,
            )
            throw UserApplicationException.Address.NotFound(e.message ?: "주소를 찾을 수 없습니다.")
        } catch (e: UserDomainException) {
            // 기타 도메인 예외 처리
            log.error(
                "도메인 예외 발생, correlationId={}, errorCode={}, error={}",
                correlationId,
                UserErrorCode.SYSTEM_ERROR.code,
                e.message,
                e,
            )
            throw UserApplicationException.Address.SystemError(e.message ?: "주소 수정 중 오류가 발생했습니다.")
        } catch (e: UserApplicationException) {
            // 이미 애플리케이션 예외로 변환된 경우 그대로 전달
            throw e
        } catch (e: Exception) {
            // 기타 예외 처리
            log.error("주소 수정 중 시스템 오류 발생, correlationId={}, error={}", correlationId, e.message, e)
            throw UserApplicationException.Address.SystemError("주소 수정 중 시스템 오류가 발생했습니다: ${e.message ?: "알 수 없는 오류"}")
        }
    }
}
