package com.restaurant.common.presentation

import com.restaurant.common.core.exception.ApplicationException
import com.restaurant.common.core.exception.DomainException
import com.restaurant.independent.outbox.infrastructure.error.OutboxException
import jakarta.persistence.OptimisticLockException
import org.slf4j.LoggerFactory
import org.slf4j.MDC
import org.springframework.beans.factory.annotation.Value
import org.springframework.http.HttpStatus
import org.springframework.http.ProblemDetail
import org.springframework.http.ResponseEntity
import org.springframework.http.converter.HttpMessageNotReadableException
import org.springframework.web.bind.MethodArgumentNotValidException
import org.springframework.web.bind.annotation.ControllerAdvice
import org.springframework.web.bind.annotation.ExceptionHandler
import org.springframework.web.context.request.WebRequest
import java.net.URI
import java.time.Instant
import java.util.UUID

/**
 * 공통 GlobalExceptionHandler - 모든 도메인 레이어에서 발생하는 예외를 처리
 */
@ControllerAdvice
class GlobalExceptionHandler(
    @Value("\${app.problem.base-url:https://api.restaurant.com/problems}") private val problemBaseUrl: String,
) {
    private val log = LoggerFactory.getLogger(GlobalExceptionHandler::class.java)

    private fun getCorrelationIdFromMDC(): String = MDC.get("correlationId") ?: UUID.randomUUID().toString()

    private fun getCorrelationIdFromRequest(request: WebRequest): String? = request.getHeader("X-Correlation-Id")

    private fun getCorrelationId(request: WebRequest): String = getCorrelationIdFromRequest(request) ?: getCorrelationIdFromMDC()

    private fun determineHttpStatusFromCode(code: String): HttpStatus =
        when {
            code == "USER-DOMAIN-001" -> HttpStatus.NOT_FOUND
            code == "USER-DOMAIN-002" -> HttpStatus.CONFLICT
            code == "USER-DOMAIN-003" -> HttpStatus.BAD_REQUEST
            code == "USER-DOMAIN-004" -> HttpStatus.NOT_FOUND
            code == "USER-DOMAIN-005" -> HttpStatus.BAD_REQUEST
            code == "USER-DOMAIN-006" -> HttpStatus.BAD_REQUEST
            code == "USER-DOMAIN-007" -> HttpStatus.BAD_REQUEST
            code == "USER-DOMAIN-008" -> HttpStatus.BAD_REQUEST
            code == "USER-DOMAIN-009" -> HttpStatus.BAD_REQUEST

            code == "USER-APPLICATION-001" -> HttpStatus.BAD_REQUEST
            code == "USER-APPLICATION-002" -> HttpStatus.UNAUTHORIZED
            code == "USER-APPLICATION-003" -> HttpStatus.INTERNAL_SERVER_ERROR
            code == "USER-APPLICATION-998" -> HttpStatus.INTERNAL_SERVER_ERROR
            code == "USER-APPLICATION-999" -> HttpStatus.INTERNAL_SERVER_ERROR

            code.startsWith("OUTBOX-") -> HttpStatus.INTERNAL_SERVER_ERROR
            code.startsWith("COMMON-SYSTEM-500") -> HttpStatus.CONFLICT
            code.startsWith("COMMON-") -> HttpStatus.INTERNAL_SERVER_ERROR
            code.contains("-NOT-FOUND") -> HttpStatus.NOT_FOUND
            code.contains("-DUPLICATE-") -> HttpStatus.CONFLICT
            code.contains("-INVALID-") -> HttpStatus.BAD_REQUEST
            code.contains("-VALIDATION-") -> HttpStatus.BAD_REQUEST
            code.endsWith("-400") -> HttpStatus.BAD_REQUEST
            code.endsWith("-404") -> HttpStatus.NOT_FOUND
            code.endsWith("-409") -> HttpStatus.CONFLICT
            code.endsWith("-422") -> HttpStatus.UNPROCESSABLE_ENTITY
            code.endsWith("-500") -> HttpStatus.INTERNAL_SERVER_ERROR
            else -> HttpStatus.INTERNAL_SERVER_ERROR
        }

    private fun createProblemDetail(
        status: HttpStatus,
        code: String,
        title: String,
        detail: String?,
        request: WebRequest,
    ): ProblemDetail {
        val correlationId = getCorrelationIdFromRequest(request) ?: getCorrelationIdFromMDC()
        return ProblemDetail.forStatus(status).apply {
            type = URI.create("$problemBaseUrl/${code.lowercase()}")
            this.title = title
            this.detail = detail ?: "오류가 발생했습니다."
            setProperty("errorCode", code)
            setProperty("timestamp", Instant.now().toString())
            setProperty("correlationId", correlationId)
        }
    }

    @ExceptionHandler(DomainException::class)
    fun handleDomainException(
        ex: DomainException,
        request: WebRequest,
    ): ResponseEntity<ProblemDetail> {
        val code = ex.errorCode.code
        val status = determineHttpStatusFromCode(code)
        val title = ex.errorCode.message
        log.warn(
            "Domain Exception Handled: correlationId={}, code={}, status={}, message={}",
            getCorrelationId(request),
            code,
            status.value(),
            ex.message,
            ex,
        )
        val problem = createProblemDetail(status, code, title, ex.message, request)
        return ResponseEntity.status(status).body(problem)
    }

    @ExceptionHandler(ApplicationException::class)
    fun handleApplicationException(
        ex: ApplicationException,
        request: WebRequest,
    ): ResponseEntity<ProblemDetail> {
        val code = ex.errorCode.code
        val status = determineHttpStatusFromCode(code)
        val title = ex.errorCode.message
        log.error(
            "Application Exception Handled: correlationId={}, code={}, status={}, message={}",
            getCorrelationId(request),
            code,
            status.value(),
            ex.message,
            ex,
        )
        val problem = createProblemDetail(status, code, title, ex.message, request)
        return ResponseEntity.status(status).body(problem)
    }

    @ExceptionHandler(OutboxException::class)
    fun handleOutboxException(
        ex: OutboxException,
        request: WebRequest,
    ): ResponseEntity<ProblemDetail> {
        val code = ex.errorCode.code
        val status = HttpStatus.INTERNAL_SERVER_ERROR
        val title = ex.errorCode.message
        log.error(
            "Outbox Exception Handled: correlationId={}, code={}, message={}",
            getCorrelationId(request),
            code,
            ex.message,
            ex,
        )
        val problem = createProblemDetail(status, code, title, ex.message, request)
        return ResponseEntity.status(status).body(problem)
    }

    @ExceptionHandler(OptimisticLockException::class)
    fun handleOptimisticLockException(
        ex: OptimisticLockException,
        request: WebRequest,
    ): ResponseEntity<ProblemDetail> {
        val code = "COMMON-SYSTEM-500"
        val status = HttpStatus.CONFLICT
        val title = "Conflict detected"
        log.warn(
            "Optimistic Lock Exception: correlationId={}, message={}",
            getCorrelationId(request),
            ex.message,
            ex,
        )
        val problem = createProblemDetail(status, code, title, ex.message ?: "데이터 충돌이 발생했습니다.", request)
        return ResponseEntity.status(status).body(problem)
    }

    @ExceptionHandler(Exception::class)
    fun handleGenericException(
        ex: Exception,
        request: WebRequest,
    ): ResponseEntity<ProblemDetail> {
        val code = "COMMON-SYSTEM-500"
        val status = HttpStatus.INTERNAL_SERVER_ERROR
        val title = "Internal server error"
        log.error(
            "Unhandled exception: correlationId={}, message={}",
            getCorrelationId(request),
            ex.message,
            ex,
        )
        val problem = createProblemDetail(status, code, title, "An unexpected error occurred", request)
        return ResponseEntity.status(status).body(problem)
    }

    @ExceptionHandler(MethodArgumentNotValidException::class)
    fun handleValidationException(
        ex: MethodArgumentNotValidException,
        request: WebRequest,
    ): ResponseEntity<ProblemDetail> {
        val code = "COMMON-VALIDATION-400"
        val status = HttpStatus.BAD_REQUEST
        val title = "Validation failed"
        val invalidParams =
            ex.bindingResult.fieldErrors.map {
                mapOf("field" to it.field, "reason" to (it.defaultMessage ?: "Invalid value"))
            }
        log.warn(
            "Validation failed: correlationId={}, errors={}",
            getCorrelationId(request),
            invalidParams,
        )
        val problem = createProblemDetail(status, code, title, ex.message, request)
        problem.setProperty("invalid-params", invalidParams)
        return ResponseEntity.status(status).body(problem)
    }

    @ExceptionHandler(HttpMessageNotReadableException::class)
    fun handleHttpMessageNotReadableException(
        ex: HttpMessageNotReadableException,
        request: WebRequest,
    ): ResponseEntity<ProblemDetail> {
        val code = "COMMON-VALIDATION-400"
        val status = HttpStatus.BAD_REQUEST
        val title = "Malformed request body"
        log.warn(
            "Malformed request body: correlationId={}, message={}",
            getCorrelationId(request),
            ex.message,
            ex,
        )
        val problem = createProblemDetail(status, code, title, "The request body is invalid or malformed", request)
        return ResponseEntity.status(status).body(problem)
    }
}
