package com.restaurant.application.user.command.handler

import com.restaurant.application.user.command.RegisterAddressCommand
import com.restaurant.application.user.error.UserApplicationException
import com.restaurant.domain.user.error.UserDomainException
import com.restaurant.domain.user.model.Address
import com.restaurant.domain.user.repository.UserRepository
import com.restaurant.domain.user.vo.UserId
import org.slf4j.LoggerFactory
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Service
public class RegisterAddressCommandHandler(
    private val userRepository: UserRepository,
) {
    private val log = LoggerFactory.getLogger(RegisterAddressCommandHandler::class.java)

    @Transactional
    fun handle(
        command: RegisterAddressCommand,
        correlationId: String? = null,
    ) {
        // VO 및 Domain Entity 생성
        val userId = UserId.fromString(command.userId)
        val address =
            Address.create(
                street = command.street,
                detail = command.detail,
                zipCode = command.zipCode,
                isDefault = command.isDefault,
            )

        log.debug("Attempting to register address, correlationId={}, userId={}", correlationId, userId)

        try {
            // 사용자 조회
            val user =
                userRepository.findById(userId)
                    ?: run {
                        log.warn("User not found for address registration, correlationId={}, userId={}", correlationId, userId)
                        // Rule 69: Domain 예외 사용
                        throw UserDomainException.User.NotFound(userId = command.userId)
                    }

            // 주소 추가 (Aggregate 호출)
            val updatedUser = user.addAddress(address)
            // 저장 (Repository)
            userRepository.save(updatedUser)

            log.info("Address registered successfully, correlationId={}, userId={}", correlationId, userId)
        } catch (de: UserDomainException) {
            // Rule 71: 로깅 시 errorCode 추가
            log.warn(
                "Domain error during address registration, correlationId={}, userId={}, errorCode={}, error: {}",
                correlationId,
                command.userId,
                de.errorCode.code,
                de.message,
            )
            throw de
        } catch (e: Exception) {
            // Rule 70: 예상치 못한 오류는 ApplicationException.SystemError
            log.error(
                "System error during address registration, correlationId={}, userId={}, error={}",
                correlationId,
                command.userId,
                e.message,
                e,
            )
            throw UserApplicationException.SystemError(e)
        }
    }
}
