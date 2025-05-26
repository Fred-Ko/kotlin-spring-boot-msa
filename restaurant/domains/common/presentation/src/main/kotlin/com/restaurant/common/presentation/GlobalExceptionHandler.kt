package com.restaurant.common.presentation

import com.restaurant.common.application.exception.ApplicationException
import com.restaurant.common.domain.error.ErrorCode
import com.restaurant.common.domain.exception.DomainException
import io.github.oshai.kotlinlogging.KotlinLogging
import jakarta.persistence.OptimisticLockException
import org.springframework.core.Ordered
import org.springframework.core.annotation.Order
import org.springframework.http.HttpStatus
import org.springframework.http.ProblemDetail
import org.springframework.web.bind.MethodArgumentNotValidException
import org.springframework.web.bind.annotation.ExceptionHandler
import org.springframework.web.bind.annotation.RestControllerAdvice
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException
import java.time.OffsetDateTime

private val log = KotlinLogging.logger {}

@RestControllerAdvice
@Order(Ordered.HIGHEST_PRECEDENCE)
class GlobalExceptionHandler {
    @ExceptionHandler(MethodArgumentNotValidException::class)
    fun handleMethodArgumentNotValid(ex: MethodArgumentNotValidException): ProblemDetail {
        val errorCode = com.restaurant.common.domain.error.CommonSystemErrorCode.VALIDATION_ERROR
        log.error("Validation failed, errorCode={}", errorCode.code, ex)
        val problemDetail = ProblemDetail.forStatus(determineHttpStatusFromErrorCode(errorCode))
        problemDetail.title = "Validation Failed"
        problemDetail.detail = ex.bindingResult.fieldErrors.joinToString(", ") { it.defaultMessage ?: it.field }
        problemDetail.setProperty(
            "invalid-params",
            ex.bindingResult.fieldErrors.map { fieldError ->
                mapOf(
                    "field" to fieldError.field,
                    "rejectedValue" to fieldError.rejectedValue,
                    "message" to (fieldError.defaultMessage ?: ""),
                )
            },
        )
        problemDetail.setProperty("errorCode", errorCode.code)
        setCommonProblemProperties(problemDetail)
        return problemDetail
    }

    @ExceptionHandler(MethodArgumentTypeMismatchException::class)
    fun handleTypeMismatch(ex: MethodArgumentTypeMismatchException): ProblemDetail {
        val errorCode = com.restaurant.common.domain.error.CommonSystemErrorCode.INVALID_REQUEST
        log.error("Type mismatch, errorCode={}", errorCode.code, ex)
        val problemDetail = ProblemDetail.forStatus(determineHttpStatusFromErrorCode(errorCode))
        problemDetail.title = "Type Mismatch"
        problemDetail.detail = ex.message ?: ""
        problemDetail.setProperty("errorCode", errorCode.code)
        setCommonProblemProperties(problemDetail)
        return problemDetail
    }

    @ExceptionHandler(DomainException::class)
    fun handleDomainException(ex: DomainException): ProblemDetail {
        log.error("Domain exception occurred, errorCode={}", ex.errorCode.code, ex)
        val problemDetail = ProblemDetail.forStatus(determineHttpStatusFromErrorCode(ex.errorCode))
        problemDetail.title = ex.errorCode.message
        problemDetail.detail = ex.message ?: ""
        problemDetail.setProperty("errorCode", ex.errorCode.code)
        setCommonProblemProperties(problemDetail)
        return problemDetail
    }

    @ExceptionHandler(ApplicationException::class)
    fun handleApplicationException(ex: ApplicationException): ProblemDetail {
        log.error("Application exception occurred, errorCode={}", ex.errorCode.code, ex)
        val problemDetail = ProblemDetail.forStatus(determineHttpStatusFromErrorCode(ex.errorCode))
        problemDetail.title = ex.errorCode.message
        problemDetail.detail = ex.message ?: ""
        problemDetail.setProperty("errorCode", ex.errorCode.code)
        setCommonProblemProperties(problemDetail)
        return problemDetail
    }

    @ExceptionHandler(OptimisticLockException::class)
    fun handleOptimisticLockException(ex: OptimisticLockException): ProblemDetail {
        val errorCode = com.restaurant.common.domain.error.CommonSystemErrorCode.OPTIMISTIC_LOCK_ERROR
        log.error("Optimistic lock error, errorCode={}", errorCode.code, ex)
        val problemDetail = ProblemDetail.forStatus(determineHttpStatusFromErrorCode(errorCode))
        problemDetail.title = errorCode.message
        problemDetail.detail = ex.message ?: ""
        problemDetail.setProperty("errorCode", errorCode.code)
        setCommonProblemProperties(problemDetail)
        return problemDetail
    }

    @ExceptionHandler(Exception::class)
    fun handleGenericException(ex: Exception): ProblemDetail {
        val errorCode = com.restaurant.common.domain.error.CommonSystemErrorCode.INTERNAL_SERVER_ERROR
        log.error("Unhandled exception, errorCode={}", errorCode.code, ex)
        val problemDetail = ProblemDetail.forStatus(determineHttpStatusFromErrorCode(errorCode))
        problemDetail.title = errorCode.message
        problemDetail.detail = ex.message ?: ""
        problemDetail.setProperty("errorCode", errorCode.code)
        setCommonProblemProperties(problemDetail)
        return problemDetail
    }

    private fun setCommonProblemProperties(problemDetail: ProblemDetail) {
        problemDetail.setProperty("timestamp", OffsetDateTime.now().toString())
    }

    /**
     * ErrorCode를 기반으로 적절한 HttpStatus를 결정합니다.
     */
    private fun determineHttpStatusFromErrorCode(errorCode: ErrorCode): HttpStatus =
        when {
            errorCode.code.contains("VALIDATION") || errorCode.code.contains("INVALID") -> HttpStatus.BAD_REQUEST
            errorCode.code.contains("NOT_FOUND") -> HttpStatus.NOT_FOUND
            errorCode.code.contains("UNAUTHORIZED") -> HttpStatus.UNAUTHORIZED
            errorCode.code.contains("FORBIDDEN") -> HttpStatus.FORBIDDEN
            errorCode.code.contains("CONFLICT") || errorCode.code.contains("DUPLICATE") -> HttpStatus.CONFLICT
            errorCode.code.contains("TOO_MANY_REQUESTS") -> HttpStatus.TOO_MANY_REQUESTS
            errorCode.code.contains("SERVICE_UNAVAILABLE") -> HttpStatus.SERVICE_UNAVAILABLE
            errorCode.code.contains("OPTIMISTIC_LOCK") -> HttpStatus.CONFLICT
            else -> HttpStatus.INTERNAL_SERVER_ERROR
        }
}
