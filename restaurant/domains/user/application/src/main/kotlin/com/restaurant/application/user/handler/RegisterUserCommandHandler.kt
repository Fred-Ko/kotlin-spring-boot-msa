package com.restaurant.application.user.handler

import com.restaurant.application.user.command.RegisterUserCommand
import com.restaurant.application.user.error.UserApplicationErrorCode
import com.restaurant.application.user.error.UserApplicationException
import com.restaurant.domain.user.aggregate.User
import com.restaurant.domain.user.error.UserDomainException
import com.restaurant.domain.user.repository.UserRepository
import com.restaurant.domain.user.vo.Email
import com.restaurant.domain.user.vo.Name
import com.restaurant.domain.user.vo.Password
import org.slf4j.LoggerFactory
import org.springframework.dao.DataIntegrityViolationException
import org.springframework.security.crypto.password.PasswordEncoder
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Service
class RegisterUserCommandHandler(
    private val userRepository: UserRepository,
    private val passwordEncoder: PasswordEncoder,
) {
    private val log = LoggerFactory.getLogger(RegisterUserCommandHandler::class.java)

    @Transactional
    fun handle(
        command: RegisterUserCommand,
        correlationId: String? = null,
    ): String {
        // Validate raw inputs and create VOs
        val email = Email.of(command.email)
        Password.validateRaw(command.password) // Validate raw password
        val name = Name.of(command.name)
        // Encode password and create Password VO
        val encodedPassword = passwordEncoder.encode(command.password)
        val password = Password.fromEncoded(encodedPassword)

        // 이메일 중복 검증 (Domain 예외 사용)
        if (userRepository.existsByEmail(email)) {
            // DuplicateEmail 예외는 ErrorCode를 내부적으로 가지므로 별도 로깅 불필요
            throw UserDomainException.User.DuplicateEmail(email = command.email)
        }

        try {
            // 사용자 생성 (이제 User 인스턴스만 반환)
            val newUser = User.create(email, password, name)

            // 사용자 저장 (Repository에서 이벤트 처리)
            val savedUser = userRepository.save(newUser)

            log.info("사용자 등록 성공, correlationId={}, email={}", correlationId, email)
            // Return UUID instead of Long
            return savedUser.id.value.toString()
        } catch (de: UserDomainException) {
            // Rule 71: 로깅 시 errorCode 추가
            log.warn(
                "Domain error during user registration, correlationId={}, errorCode={}, error: {}",
                correlationId,
                de.errorCode.code,
                de.message,
            )
            throw de
        } catch (dive: DataIntegrityViolationException) {
            // Catch potential unique constraint violations not caught by existsByEmail (race condition)
            log.error(
                "Data integrity violation during user registration, possibly duplicate email race condition, correlationId={}, error: {}",
                correlationId,
                dive.message,
                dive,
            )
            // Re-throw as duplicate email domain exception
            throw UserDomainException.User.DuplicateEmail(email.value)
        } catch (e: Exception) {
            // 기타 예상치 못한 오류
            log.error(
                "사용자 등록 중 시스템 오류 발생, correlationId={}, errorCode={}, error={}",
                correlationId,
                UserApplicationErrorCode.SYSTEM_ERROR.code,
                e.message,
                e,
            )
            // Wrap as ApplicationException.SystemError before propagating
            throw UserApplicationException.SystemError(e)
        }
    }
}
