package com.restaurant.application.user.command.handler

import com.restaurant.application.user.command.DeleteAddressCommand
import com.restaurant.application.user.error.UserApplicationException
import com.restaurant.domain.user.error.UserDomainException
import com.restaurant.domain.user.repository.UserRepository
import com.restaurant.domain.user.vo.AddressId
import com.restaurant.domain.user.vo.UserId
import org.slf4j.LoggerFactory
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Service
public class DeleteAddressCommandHandler(
    private val userRepository: UserRepository,
) {
    private val log = LoggerFactory.getLogger(DeleteAddressCommandHandler::class.java)

    @Transactional
    fun handle(
        command: DeleteAddressCommand,
        correlationId: String? = null,
    ) {
        // VO 생성
        val userId = UserId.fromString(command.userId)
        val addressId = AddressId.fromString(command.addressId)

        log.debug("Attempting to delete address, correlationId={}, userId={}, addressId={}", correlationId, userId, addressId)

        try {
            // 사용자 조회
            val user =
                userRepository.findById(userId)
                    ?: run {
                        log.warn("User not found for address deletion, correlationId={}, userId={}", correlationId, userId)
                        // Rule 69: Domain 예외 사용
                        throw UserDomainException.User.NotFound(userId = command.userId)
                    }

            // 주소 삭제 (Aggregate 호출 - 내부에서 AddressNotFound 예외 발생 가능)
            val updatedUser = user.removeAddress(addressId)
            // 저장
            userRepository.save(updatedUser)

            log.info("Address deleted successfully, correlationId={}, userId={}, addressId={}", correlationId, userId, addressId)
        } catch (de: UserDomainException) {
            // Rule 71: 로깅 시 errorCode 추가
            log.warn(
                "Domain error during address deletion, correlationId={}, userId={}, addressId={}, errorCode={}, error: {}",
                correlationId,
                command.userId,
                command.addressId,
                de.errorCode.code,
                de.message,
            )
            throw de
        } catch (e: Exception) {
            // Rule 70: 예상치 못한 오류는 ApplicationException.SystemError
            log.error(
                "System error during address deletion, correlationId={}, userId={}, addressId={}, error={}",
                correlationId,
                command.userId,
                command.addressId,
                e.message,
                e,
            )
            throw UserApplicationException.SystemError(e)
        }
    }
}
