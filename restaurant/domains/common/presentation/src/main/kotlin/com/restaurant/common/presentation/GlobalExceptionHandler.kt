package com.restaurant.common.presentation

import com.restaurant.common.application.exception.ApplicationException
import com.restaurant.common.domain.exception.DomainException
import jakarta.persistence.OptimisticLockException
import mu.KotlinLogging
import org.slf4j.MDC
import org.springframework.core.Ordered
import org.springframework.core.annotation.Order
import org.springframework.http.HttpStatus
import org.springframework.http.ProblemDetail
import org.springframework.web.bind.MethodArgumentNotValidException
import org.springframework.web.bind.annotation.ExceptionHandler
import org.springframework.web.bind.annotation.RestControllerAdvice
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler
import java.time.OffsetDateTime
import java.util.UUID

private val log = KotlinLogging.logger {}

@RestControllerAdvice
@Order(Ordered.HIGHEST_PRECEDENCE)
class GlobalExceptionHandler : ResponseEntityExceptionHandler() {
    @ExceptionHandler(MethodArgumentNotValidException::class)
    fun handleMethodArgumentNotValid(ex: MethodArgumentNotValidException): ProblemDetail {
        val errorCode = com.restaurant.common.domain.error.CommonSystemErrorCode.VALIDATION_ERROR
        log.error("Validation failed, errorCode={}", errorCode.code, ex)
        val problemDetail = ProblemDetail.forStatus(errorCode.defaultHttpStatus)
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
        val problemDetail = ProblemDetail.forStatus(errorCode.defaultHttpStatus)
        problemDetail.title = "Type Mismatch"
        problemDetail.detail = ex.message ?: ""
        problemDetail.setProperty("errorCode", errorCode.code)
        setCommonProblemProperties(problemDetail)
        return problemDetail
    }

    @ExceptionHandler(DomainException::class)
    fun handleDomainException(ex: DomainException): ProblemDetail {
        log.error("Domain exception occurred, errorCode={}", ex.errorCode.code, ex)
        val problemDetail = ProblemDetail.forStatus(ex.errorCode.defaultHttpStatus)
        problemDetail.title = ex.errorCode.message
        problemDetail.detail = ex.message ?: ""
        problemDetail.setProperty("errorCode", ex.errorCode.code)
        setCommonProblemProperties(problemDetail)
        return problemDetail
    }

    @ExceptionHandler(ApplicationException::class)
    fun handleApplicationException(ex: ApplicationException): ProblemDetail {
        log.error("Application exception occurred, errorCode={}", ex.errorCode.code, ex)
        val problemDetail = ProblemDetail.forStatus(ex.errorCode.defaultHttpStatus)
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
        val problemDetail = ProblemDetail.forStatus(errorCode.defaultHttpStatus)
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
        val problemDetail = ProblemDetail.forStatus(errorCode.defaultHttpStatus)
        problemDetail.title = errorCode.message
        problemDetail.detail = ex.message ?: ""
        problemDetail.setProperty("errorCode", errorCode.code)
        setCommonProblemProperties(problemDetail)
        return problemDetail
    }

    private fun setCommonProblemProperties(problemDetail: ProblemDetail) {
        problemDetail.setProperty("timestamp", OffsetDateTime.now().toString())
    }

    // 매핑 함수 제거: 각 ErrorCode의 defaultHttpStatus를 직접 사용하므로 불필요

}
