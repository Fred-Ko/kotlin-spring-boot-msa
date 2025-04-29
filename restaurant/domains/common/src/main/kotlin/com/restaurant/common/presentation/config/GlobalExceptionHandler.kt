package com.restaurant.common.presentation.config

import com.restaurant.common.error.CommonSystemErrorCode
import org.slf4j.MDC
import org.springframework.core.Ordered
import org.springframework.core.annotation.Order
import org.springframework.http.HttpStatus
import org.springframework.http.ProblemDetail
import org.springframework.web.bind.MethodArgumentNotValidException
import org.springframework.web.bind.annotation.ExceptionHandler
import org.springframework.web.bind.annotation.RestControllerAdvice
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException

@Order(Ordered.HIGHEST_PRECEDENCE)
@RestControllerAdvice
class GlobalExceptionHandler {
    private fun correlationId(): String = MDC.get("X-Correlation-Id") ?: ""

    @ExceptionHandler(MethodArgumentNotValidException::class)
    fun handleValidation(ex: MethodArgumentNotValidException): ProblemDetail {
        val pd = ProblemDetail.forStatus(HttpStatus.BAD_REQUEST)
        pd.detail = ex.bindingResult.allErrors.joinToString(", ") { it.defaultMessage ?: it.code ?: "Invalid" }
        pd.properties?.set("invalid-params", ex.fieldErrors.map { it.field })
        pd.properties?.set("correlationId", correlationId())
        pd.properties?.set("errorCode", CommonSystemErrorCode.VALIDATION_ERROR.code)
        return pd
    }

    @ExceptionHandler(MethodArgumentTypeMismatchException::class)
    fun handleTypeMismatch(ex: MethodArgumentTypeMismatchException): ProblemDetail {
        val pd = ProblemDetail.forStatus(HttpStatus.BAD_REQUEST)
        pd.detail = "Type mismatch for parameter '${ex.name}'"
        pd.properties?.set("correlationId", correlationId())
        pd.properties?.set("errorCode", CommonSystemErrorCode.INVALID_REQUEST.code)
        return pd
    }

    @ExceptionHandler(Exception::class)
    fun handleAll(ex: Exception): ProblemDetail {
        val pd = ProblemDetail.forStatus(HttpStatus.INTERNAL_SERVER_ERROR)
        pd.detail = ex.message ?: "An unexpected error occurred"
        pd.properties?.set("correlationId", correlationId())
        pd.properties?.set("errorCode", CommonSystemErrorCode.INTERNAL_ERROR.code)
        return pd
    }
}
