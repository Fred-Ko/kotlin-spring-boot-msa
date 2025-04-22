package com.restaurant.application.user.handler

import com.restaurant.application.user.command.DeleteAddressCommand
import com.restaurant.application.user.error.UserApplicationException
import com.restaurant.domain.user.error.UserDomainException
import com.restaurant.domain.user.repository.UserRepository
import com.restaurant.domain.user.vo.AddressId
import com.restaurant.domain.user.vo.UserId
import org.slf4j.LoggerFactory
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

/**
 * 사용자 주소 삭제 커맨드 핸들러
 */
@Service
class DeleteUserAddressCommandHandler(
    private val userRepository: UserRepository,
) {
    private val log = LoggerFactory.getLogger(DeleteUserAddressCommandHandler::class.java)

    @Transactional
    fun handle(
        command: DeleteAddressCommand,
        correlationId: String? = null,
    ) {
        // Rule 14, 61: VO 생성
        val userId = UserId.fromString(command.userId)
        val addressId = AddressId.fromString(command.addressId)

        log.debug(
            "Attempting to delete address, correlationId={}, userId={}, addressId={}",
            correlationId,
            userId,
            addressId,
        )

        try {
            // 사용자 조회
            val user =
                userRepository.findById(userId) ?: run {
                    log.warn(
                        "User not found for address deletion, correlationId={}, userId={}",
                        correlationId,
                        userId,
                    )
                    throw UserDomainException.User.NotFound(userId = command.userId)
                }

            // 주소 삭제 (Domain 로직 호출)
            val updatedUser = user.removeAddress(addressId)

            // 사용자 저장
            userRepository.save(updatedUser)

            log.info(
                "Address deleted successfully, correlationId={}, userId={}, addressId={}",
                correlationId,
                userId,
                addressId,
            )
        } catch (de: UserDomainException) {
            // Rule 71: 로깅 시 errorCode 추가
            log.warn(
                "Domain error during address deletion, correlationId={}, userId={}, addressId={}, errorCode={}, error={}",
                correlationId,
                userId,
                addressId,
                de.errorCode.code,
                de.message,
            )
            throw de
        } catch (e: Exception) {
            // 예상치 못한 시스템 오류
            log.error(
                "System error during address deletion, correlationId={}, userId={}, addressId={}, error={}",
                correlationId,
                userId,
                addressId,
                e.message,
                e,
            )
            throw UserApplicationException.SystemError(e)
        }
    }
}
