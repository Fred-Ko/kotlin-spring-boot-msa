package com.restaurant.application.user.command.handler

import com.restaurant.application.user.command.RegisterUserCommand
import com.restaurant.application.user.exception.UserApplicationException
import com.restaurant.domain.user.aggregate.User
import com.restaurant.domain.user.repository.UserRepository
import com.restaurant.domain.user.vo.Email
import com.restaurant.domain.user.vo.Name
import com.restaurant.domain.user.vo.Password
import org.slf4j.LoggerFactory
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Service
class RegisterUserCommandHandler(
    private val userRepository: UserRepository,
) {
    private val log = LoggerFactory.getLogger(RegisterUserCommandHandler::class.java)

    @Transactional
    fun handle(
        command: RegisterUserCommand,
        correlationId: String? = null,
    ) {
        // 이메일 유효성 검증
        val email =
            try {
                Email.of(command.email)
            } catch (e: IllegalArgumentException) {
                log.error("유효하지 않은 이메일 형식, correlationId={}, email={}", correlationId, command.email, e)
                throw UserApplicationException.Registration.InvalidInput("유효하지 않은 이메일 형식입니다: ${command.email}")
            }

        // 이메일 중복 검증
        if (userRepository.existsByEmail(email)) {
            log.error("이메일 중복, correlationId={}, email={}", correlationId, command.email)
            throw UserApplicationException.Registration.DuplicateEmail("이미 등록된 이메일입니다: ${command.email}")
        }

        // 비밀번호 유효성 검증
        val password =
            try {
                Password.of(command.password)
            } catch (e: IllegalArgumentException) {
                log.error("유효하지 않은 비밀번호 형식, correlationId={}", correlationId, e)
                throw UserApplicationException.Registration.InvalidInput("유효하지 않은 비밀번호 형식입니다.")
            }

        // 이름 유효성 검증
        val name =
            try {
                Name.of(command.name)
            } catch (e: IllegalArgumentException) {
                log.error("유효하지 않은 이름 형식, correlationId={}, name={}", correlationId, command.name, e)
                throw UserApplicationException.Registration.InvalidInput("유효하지 않은 이름 형식입니다: ${command.name}")
            }

        try {
            // 사용자 생성 및 저장
            val user = User.create(email, password, name)
            userRepository.save(user)

            log.info("사용자 등록 성공, correlationId={}, email={}", correlationId, email)
        } catch (e: Exception) {
            // 시스템 오류 발생 시
            log.error("사용자 등록 중 시스템 오류 발생, correlationId={}, error={}", correlationId, e.message, e)
            throw UserApplicationException.Registration.SystemError("사용자 등록 중 시스템 오류가 발생했습니다: ${e.message ?: "알 수 없는 오류"}")
        }
    }
}
