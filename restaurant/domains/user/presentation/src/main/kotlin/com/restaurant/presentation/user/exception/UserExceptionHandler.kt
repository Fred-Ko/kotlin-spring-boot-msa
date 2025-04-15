package com.restaurant.presentation.user.exception

import com.restaurant.application.user.exception.UserApplicationException
import org.slf4j.LoggerFactory
import org.springframework.beans.factory.annotation.Value
import org.springframework.http.ProblemDetail
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.ControllerAdvice
import org.springframework.web.bind.annotation.ExceptionHandler
import java.net.URI
import java.time.Instant
import java.util.UUID

@ControllerAdvice
class UserExceptionHandler(
    @Value("\${app.problem.base-url:https://api.restaurant.com/problems}") private val problemBaseUrl: String,
) {
    private val log = LoggerFactory.getLogger(UserExceptionHandler::class.java)

    @ExceptionHandler(UserApplicationException::class)
    fun handleUserApplicationException(ex: UserApplicationException): ResponseEntity<ProblemDetail> {
        val correlationId = UUID.randomUUID().toString()

        log.error(
            "사용자 애플리케이션 예외 발생, correlationId={}, errorCode={}, error={}",
            correlationId,
            ex.errorCode.code,
            ex.message,
            ex,
        )

        val problem =
            ProblemDetail.forStatus(ex.errorCode.status).apply {
                type = URI.create("$problemBaseUrl/${ex.errorCode.code.lowercase()}")
                title = ex.errorCode.code
                detail = ex.message
                setProperty("errorCode", ex.errorCode.code)
                setProperty("correlationId", correlationId)
                setProperty("timestamp", Instant.now().toString())
            }

        return ResponseEntity.status(ex.errorCode.status).body(problem)
    }

    @ExceptionHandler(UserApplicationException.Registration::class)
    fun handleRegistrationException(ex: UserApplicationException.Registration): ResponseEntity<ProblemDetail> {
        val correlationId = UUID.randomUUID().toString()

        log.error(
            "사용자 등록 예외 발생, correlationId={}, errorCode={}, error={}",
            correlationId,
            ex.errorCode.code,
            ex.message,
            ex,
        )

        val problem =
            ProblemDetail.forStatus(ex.errorCode.status).apply {
                type = URI.create("$problemBaseUrl/${ex.errorCode.code.lowercase()}")
                title = ex.errorCode.code
                detail = ex.message
                instance = URI.create("/api/v1/users/register")
                setProperty("errorCode", ex.errorCode.code)
                setProperty("correlationId", correlationId)
                setProperty("timestamp", Instant.now().toString())
            }

        return ResponseEntity.status(ex.errorCode.status).body(problem)
    }

    @ExceptionHandler(UserApplicationException.Authentication::class)
    fun handleAuthenticationException(ex: UserApplicationException.Authentication): ResponseEntity<ProblemDetail> {
        val correlationId = UUID.randomUUID().toString()

        log.error(
            "사용자 인증 예외 발생, correlationId={}, errorCode={}, error={}",
            correlationId,
            ex.errorCode.code,
            ex.message,
            ex,
        )

        val problem =
            ProblemDetail.forStatus(ex.errorCode.status).apply {
                type = URI.create("$problemBaseUrl/${ex.errorCode.code.lowercase()}")
                title = ex.errorCode.code
                detail = ex.message
                instance = URI.create("/api/v1/users/login")
                setProperty("errorCode", ex.errorCode.code)
                setProperty("correlationId", correlationId)
                setProperty("timestamp", Instant.now().toString())
            }

        return ResponseEntity.status(ex.errorCode.status).body(problem)
    }

    @ExceptionHandler(UserApplicationException.Profile::class)
    fun handleProfileException(ex: UserApplicationException.Profile): ResponseEntity<ProblemDetail> {
        val correlationId = UUID.randomUUID().toString()

        log.error(
            "사용자 프로필 예외 발생, correlationId={}, errorCode={}, error={}",
            correlationId,
            ex.errorCode.code,
            ex.message,
            ex,
        )

        val problem =
            ProblemDetail.forStatus(ex.errorCode.status).apply {
                type = URI.create("$problemBaseUrl/${ex.errorCode.code.lowercase()}")
                title = ex.errorCode.code
                detail = ex.message
                instance = URI.create("/api/v1/users/profile")
                setProperty("errorCode", ex.errorCode.code)
                setProperty("correlationId", correlationId)
                setProperty("timestamp", Instant.now().toString())
            }

        return ResponseEntity.status(ex.errorCode.status).body(problem)
    }

    @ExceptionHandler(UserApplicationException.Password::class)
    fun handlePasswordException(ex: UserApplicationException.Password): ResponseEntity<ProblemDetail> {
        val correlationId = UUID.randomUUID().toString()

        log.error(
            "비밀번호 변경 예외 발생, correlationId={}, errorCode={}, error={}",
            correlationId,
            ex.errorCode.code,
            ex.message,
            ex,
        )

        val problem =
            ProblemDetail.forStatus(ex.errorCode.status).apply {
                type = URI.create("$problemBaseUrl/${ex.errorCode.code.lowercase()}")
                title = ex.errorCode.code
                detail = ex.message
                instance = URI.create("/api/v1/users/password")
                setProperty("errorCode", ex.errorCode.code)
                setProperty("correlationId", correlationId)
                setProperty("timestamp", Instant.now().toString())
            }

        return ResponseEntity.status(ex.errorCode.status).body(problem)
    }

    @ExceptionHandler(UserApplicationException.Deletion::class)
    fun handleDeletionException(ex: UserApplicationException.Deletion): ResponseEntity<ProblemDetail> {
        val correlationId = UUID.randomUUID().toString()

        log.error(
            "회원 탈퇴 예외 발생, correlationId={}, errorCode={}, error={}",
            correlationId,
            ex.errorCode.code,
            ex.message,
            ex,
        )

        val problem =
            ProblemDetail.forStatus(ex.errorCode.status).apply {
                type = URI.create("$problemBaseUrl/${ex.errorCode.code.lowercase()}")
                title = ex.errorCode.code
                detail = ex.message
                instance = URI.create("/api/v1/users")
                setProperty("errorCode", ex.errorCode.code)
                setProperty("correlationId", correlationId)
                setProperty("timestamp", Instant.now().toString())
            }

        return ResponseEntity.status(ex.errorCode.status).body(problem)
    }

    @ExceptionHandler(UserApplicationException.Query::class)
    fun handleQueryException(ex: UserApplicationException.Query): ResponseEntity<ProblemDetail> {
        val correlationId = UUID.randomUUID().toString()

        log.error(
            "사용자 조회 예외 발생, correlationId={}, errorCode={}, error={}",
            correlationId,
            ex.errorCode.code,
            ex.message,
            ex,
        )

        val problem =
            ProblemDetail.forStatus(ex.errorCode.status).apply {
                type = URI.create("$problemBaseUrl/${ex.errorCode.code.lowercase()}")
                title = ex.errorCode.code
                detail = ex.message
                instance = URI.create("/api/v1/users")
                setProperty("errorCode", ex.errorCode.code)
                setProperty("correlationId", correlationId)
                setProperty("timestamp", Instant.now().toString())
            }

        return ResponseEntity.status(ex.errorCode.status).body(problem)
    }

    @ExceptionHandler(UserApplicationException.Address::class)
    fun handleAddressException(ex: UserApplicationException.Address): ResponseEntity<ProblemDetail> {
        val correlationId = UUID.randomUUID().toString()

        log.error(
            "주소 관련 예외 발생, correlationId={}, errorCode={}, error={}",
            correlationId,
            ex.errorCode.code,
            ex.message,
            ex,
        )

        val problem =
            ProblemDetail.forStatus(ex.errorCode.status).apply {
                type = URI.create("$problemBaseUrl/${ex.errorCode.code.lowercase()}")
                title = ex.errorCode.code
                detail = ex.message
                instance = URI.create("/api/v1/users/addresses")
                setProperty("errorCode", ex.errorCode.code)
                setProperty("correlationId", correlationId)
                setProperty("timestamp", Instant.now().toString())
            }

        return ResponseEntity.status(ex.errorCode.status).body(problem)
    }
}
