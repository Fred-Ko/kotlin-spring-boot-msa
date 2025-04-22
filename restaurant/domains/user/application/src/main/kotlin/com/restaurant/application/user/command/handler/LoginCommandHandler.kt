package com.restaurant.application.user.command.handler

import com.restaurant.application.user.command.LoginCommand
import com.restaurant.application.user.dto.LoginResult
import com.restaurant.application.user.error.UserApplicationException
import com.restaurant.domain.user.repository.UserRepository
import com.restaurant.domain.user.vo.Email
import io.jsonwebtoken.Jwts
import io.jsonwebtoken.security.Keys
import org.slf4j.LoggerFactory
import org.springframework.beans.factory.annotation.Value
import org.springframework.security.crypto.password.PasswordEncoder
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import java.nio.charset.StandardCharsets
import java.time.Instant
import java.time.temporal.ChronoUnit
import java.util.Date

@Service
class LoginCommandHandler(
    private val userRepository: UserRepository,
    private val passwordEncoder: PasswordEncoder,
    @Value("\${jwt.secret:defaultSecretKeyForLocalDevelopmentOnly}") private val jwtSecret: String,
    @Value("\${jwt.access-token-expiration:30}") private val accessTokenExpirationMinutes: Long = 30,
    @Value("\${jwt.refresh-token-expiration:43200}") private val refreshTokenExpirationMinutes: Long = 43200,
) {
    private val log = LoggerFactory.getLogger(LoginCommandHandler::class.java)

    @Transactional(readOnly = true)
    fun handle(
        command: LoginCommand,
        correlationId: String? = null,
    ): LoginResult {
        // VO 생성
        val email = Email.of(command.email)
        log.debug("Attempting to login user, correlationId={}, email={}", correlationId, email)

        try {
            // 사용자 조회
            val user =
                userRepository.findByEmail(email)
                    ?: run {
                        log.warn("User not found for login, correlationId={}, email={}", correlationId, email)
                        // 사용자가 없는 경우에도 동일한 인증 실패 메시지를 반환하여 정보 노출 방지
                        throw UserApplicationException.AuthenticationFailed()
                    }

            // 비밀번호 검증
            if (!passwordEncoder.matches(command.password, user.password.encodedValue)) {
                log.warn("Authentication failed for user with email: {}, correlationId={}", command.email, correlationId)
                throw UserApplicationException.AuthenticationFailed()
            }

            // 로그인 성공 처리 - JWT 토큰 생성
            val userId = user.id.value.toString()
            log.info("User logged in successfully, correlationId={}, userId={}", correlationId, userId)

            // JWT 토큰 생성 로직
            val now = Instant.now()

            // Access Token 생성
            val accessTokenExpiration = Date.from(now.plus(accessTokenExpirationMinutes, ChronoUnit.MINUTES))
            val key = Keys.hmacShaKeyFor(jwtSecret.toByteArray(StandardCharsets.UTF_8))

            val accessToken =
                Jwts
                    .builder()
                    .subject(userId)
                    .claim("email", user.email.value)
                    .claim("name", user.name)
                    .issuedAt(Date.from(now))
                    .expiration(accessTokenExpiration)
                    .signWith(key)
                    .compact()

            // Refresh Token 생성
            val refreshTokenExpiration = Date.from(now.plus(refreshTokenExpirationMinutes, ChronoUnit.MINUTES))
            val refreshToken =
                Jwts
                    .builder()
                    .subject(userId)
                    .claim("type", "refresh")
                    .issuedAt(Date.from(now))
                    .expiration(refreshTokenExpiration)
                    .signWith(key)
                    .compact()

            return LoginResult(
                userId = userId,
                accessToken = accessToken,
                refreshToken = refreshToken,
            )
        } catch (e: Exception) {
            when (e) {
                is UserApplicationException -> {
                    // Rule 71: 로깅 시 errorCode 추가
                    log.warn(
                        "Application error during login, correlationId={}, email={}, errorCode={}, error: {}",
                        correlationId,
                        command.email,
                        e.errorCode.code,
                        e.message,
                    )
                    throw e
                }
                else -> {
                    // 예상치 못한 오류 처리
                    log.error("System error during login, correlationId={}, email={}, error={}", correlationId, command.email, e.message, e)
                    throw UserApplicationException.SystemError(e)
                }
            }
        }
    }
}
