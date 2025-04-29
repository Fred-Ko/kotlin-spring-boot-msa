package com.restaurant.user.application.usecase

import com.restaurant.user.application.dto.command.LoginCommand
import com.restaurant.user.application.dto.query.LoginResult
import com.restaurant.user.application.error.UserApplicationErrorCode
import com.restaurant.user.application.exception.UserApplicationException
import com.restaurant.user.application.port.input.LoginUseCase
import com.restaurant.user.domain.repository.UserRepository
import com.restaurant.user.domain.vo.Email
import mu.KotlinLogging
import org.springframework.security.crypto.password.PasswordEncoder
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

private val log = KotlinLogging.logger {}

@Service
class LoginCommandHandler(
    private val userRepository: UserRepository,
    private val passwordEncoder: PasswordEncoder,
    // private val jwtTokenProvider: JwtTokenProvider // Assuming JWT provider exists
) : LoginUseCase {
    @Transactional(readOnly = true)
    override fun login(command: LoginCommand): LoginResult {
        log.info { "Processing login command for email: ${command.email}" }

        val email = Email.of(command.email) // DomainException.Validation can occur here and should propagate

        val user =
            userRepository.findByEmail(email)
                ?: throw UserApplicationException.UserNotFound(UserApplicationErrorCode.USER_NOT_FOUND_BY_EMAIL, command.email)

        // Validate password using PasswordEncoder (Application layer responsibility)
        if (!passwordEncoder.matches(command.password, user.password.value)) {
            log.warn { "Invalid password attempt for email: ${command.email}" }
            throw UserApplicationException.InvalidCredentials(UserApplicationErrorCode.INVALID_CREDENTIALS)
        }

        if (!user.isActive()) {
            log.warn { "Attempt to login with inactive user: ${command.email}" }
            throw UserApplicationException.UserInactive(UserApplicationErrorCode.USER_INACTIVE, user.id.value.toString())
        }

        // Generate Token (Example - Assuming JwtTokenProvider)
        // val token = jwtTokenProvider.generateToken(user.id.value.toString(), user.userType)
        val fakeToken = "fake-jwt-token-for-${user.id.value}" // Placeholder

        log.info { "User logged in successfully: ${user.email.value}" }
        return LoginResult(
            userId = user.id.value.toString(),
            username = user.username.value,
            accessToken = fakeToken,
            refreshToken = "fake-refresh-token",
        )
    }
}
