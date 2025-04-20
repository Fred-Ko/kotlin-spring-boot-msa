package com.restaurant.application.user.command.handler

import com.restaurant.application.user.command.UpdateProfileCommand
import com.restaurant.application.user.exception.UserApplicationException
import com.restaurant.domain.user.exception.UserDomainException
import com.restaurant.domain.user.repository.UserRepository
import com.restaurant.domain.user.vo.Name
import com.restaurant.domain.user.vo.UserId
import org.slf4j.LoggerFactory
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Service
public class UpdateProfileCommandHandler(
    private val userRepository: UserRepository,
) {
    private val log = LoggerFactory.getLogger(UpdateProfileCommandHandler::class.java)

    @Transactional
    fun handle(
        command: UpdateProfileCommand,
        correlationId: String? = null,
    ) {
        // VO 생성
        val userId = UserId.fromString(command.userId)
        val name = Name.of(command.name)

        try {
            // 사용자 조회 - DomainException (NotFound)는 상위로 전파
            val user =
                userRepository.findById(userId)
                    ?: run {
                        log.warn("사용자를 찾을 수 없음, correlationId={}, userId={}", correlationId, userId)
                        throw UserDomainException.User.NotFound(userId = command.userId)
                    }

            // 프로필 업데이트
            val updatedUser = user.updateProfile(name = name)
            userRepository.save(updatedUser)

            log.info("사용자 프로필 업데이트 성공, correlationId={}, userId={}", correlationId, userId)
        } catch (de: UserDomainException) {
            log.error(
                "사용자 프로필 업데이트 중 도메인 오류 발생, correlationId={}, errorCode={}, error={}",
                correlationId,
                de.errorCode.code,
                de.message,
                de,
            )
            throw de
        } catch (e: Exception) {
            log.error(
                "사용자 프로필 업데이트 중 시스템 오류 발생, correlationId={}, error={}",
                correlationId,
                e.message,
                e,
            )
            throw UserApplicationException.SystemError(e)
        }
    }
}
