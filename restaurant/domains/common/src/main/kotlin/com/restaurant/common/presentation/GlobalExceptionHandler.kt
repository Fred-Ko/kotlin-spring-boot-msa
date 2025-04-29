package com.restaurant.common.presentation

import com.restaurant.common.exception.ApplicationException
import com.restaurant.common.exception.DomainException
import jakarta.persistence.OptimisticLockException
import mu.KotlinLogging
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
        val problemDetail = ProblemDetail.forStatus(HttpStatus.BAD_REQUEST)
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
        setCommonProblemProperties(problemDetail)
        return problemDetail
    }

    @ExceptionHandler(MethodArgumentTypeMismatchException::class)
    fun handleTypeMismatch(ex: MethodArgumentTypeMismatchException): ProblemDetail {
        val problemDetail = ProblemDetail.forStatus(HttpStatus.BAD_REQUEST)
        problemDetail.title = "Type Mismatch"
        problemDetail.detail = ex.message
        setCommonProblemProperties(problemDetail)
        return problemDetail
    }

    @ExceptionHandler(DomainException::class)
    fun handleDomainException(ex: DomainException): ProblemDetail {
        val problemDetail = ProblemDetail.forStatus(mapDomainExceptionToStatus(ex))
        problemDetail.title = ex.errorCode.message
        problemDetail.detail = ex.message
        problemDetail.setProperty("errorCode", ex.errorCode.code)
        setCommonProblemProperties(problemDetail)
        return problemDetail
    }

    @ExceptionHandler(ApplicationException::class)
    fun handleApplicationException(ex: ApplicationException): ProblemDetail {
        val problemDetail = ProblemDetail.forStatus(mapApplicationExceptionToStatus(ex))
        problemDetail.title = ex.errorCode.message
        problemDetail.detail = ex.message
        problemDetail.setProperty("errorCode", ex.errorCode.code)
        setCommonProblemProperties(problemDetail)
        return problemDetail
    }

    @ExceptionHandler(OptimisticLockException::class)
    fun handleOptimisticLockException(ex: OptimisticLockException): ProblemDetail {
        val problemDetail = ProblemDetail.forStatus(HttpStatus.CONFLICT)
        problemDetail.title = "Optimistic Lock Error"
        problemDetail.detail = ex.message
        problemDetail.setProperty("errorCode", "COMMON-SYSTEM-OPTIMISTIC-LOCK-ERROR")
        setCommonProblemProperties(problemDetail)
        return problemDetail
    }

    @ExceptionHandler(Exception::class)
    fun handleGenericException(ex: Exception): ProblemDetail {
        log.error(ex) { "Unhandled exception: ${ex.message}" }
        val problemDetail = ProblemDetail.forStatus(HttpStatus.INTERNAL_SERVER_ERROR)
        problemDetail.title = "Internal Server Error"
        problemDetail.detail = ex.message
        setCommonProblemProperties(problemDetail)
        return problemDetail
    }

    private fun setCommonProblemProperties(problemDetail: ProblemDetail) {
        problemDetail.setProperty("timestamp", OffsetDateTime.now().toString())
        problemDetail.setProperty("correlationId", getCorrelationId())
    }

    private fun getCorrelationId(): String =
        try {
            val mdc = org.slf4j.MDC.getCopyOfContextMap()
            mdc?.get("correlationId") ?: UUID.randomUUID().toString()
        } catch (e: Exception) {
            UUID.randomUUID().toString()
        }

    private fun mapDomainExceptionToStatus(ex: DomainException): HttpStatus =
        when (ex.errorCode.code) {
            "USER-DOMAIN-NOT-FOUND" -> HttpStatus.NOT_FOUND
            "USER-DOMAIN-VALIDATION" -> HttpStatus.BAD_REQUEST
            else -> HttpStatus.BAD_REQUEST
        }

    private fun mapApplicationExceptionToStatus(ex: ApplicationException): HttpStatus =
        when (ex.errorCode.code) {
            "USER-APPLICATION-BAD-REQUEST" -> HttpStatus.BAD_REQUEST
            else -> HttpStatus.INTERNAL_SERVER_ERROR
        }
}
