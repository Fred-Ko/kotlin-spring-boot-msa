package com.restaurant.common.presentation

import com.restaurant.common.application.exception.ApplicationException
import com.restaurant.common.domain.error.CommonSystemErrorCode
import com.restaurant.common.domain.error.ErrorCode
import com.restaurant.common.domain.exception.DomainException
import com.restaurant.outbox.infrastructure.error.OutboxErrorCodes
import com.restaurant.outbox.infrastructure.exception.OutboxException
import io.github.oshai.kotlinlogging.KotlinLogging
import jakarta.persistence.OptimisticLockException
import jakarta.servlet.http.HttpServletRequest
import org.springframework.http.HttpStatus
import org.springframework.http.ProblemDetail
import org.springframework.http.converter.HttpMessageNotReadableException
import org.springframework.web.bind.MethodArgumentNotValidException
import org.springframework.web.bind.annotation.ExceptionHandler
import org.springframework.web.bind.annotation.RestControllerAdvice
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException
import org.springframework.web.servlet.resource.NoResourceFoundException
import java.net.URI
import java.time.Instant

private val log = KotlinLogging.logger {}

@RestControllerAdvice
class GlobalExceptionHandler {
    @ExceptionHandler(NoResourceFoundException::class)
    fun handleNoResourceFoundException(
        ex: NoResourceFoundException,
        request: HttpServletRequest,
    ): ProblemDetail {
        if (request.requestURI.contains("favicon.ico") ||
            request.requestURI.contains(".css") ||
            request.requestURI.contains(".js") ||
            request.requestURI.contains(".png") ||
            request.requestURI.contains(".jpg") ||
            request.requestURI.contains(".gif")
        ) {
            log.debug { "Static resource not found: ${request.requestURI}" }
        } else {
            val errorCode = CommonSystemErrorCode.RESOURCE_NOT_FOUND
            log.warn { "Resource not found, errorCode=${errorCode.code}, uri=${request.requestURI}" }
        }

        val errorCode = CommonSystemErrorCode.RESOURCE_NOT_FOUND
        val problemDetail = ProblemDetail.forStatus(HttpStatus.NOT_FOUND)
        problemDetail.title = "Resource Not Found"
        problemDetail.detail = "The requested resource '${request.requestURI}' was not found."
        problemDetail.setProperty("errorCode", errorCode.code)
        setCommonProblemProperties(problemDetail, request)
        return problemDetail
    }

    @ExceptionHandler(MethodArgumentNotValidException::class)
    fun handleMethodArgumentNotValidException(
        ex: MethodArgumentNotValidException,
        request: HttpServletRequest,
    ): ProblemDetail {
        val errorCode = CommonSystemErrorCode.VALIDATION_ERROR
        log.error(ex) { "Validation failed, errorCode=${errorCode.code}" }
        val problemDetail = ProblemDetail.forStatus(determineHttpStatusFromErrorCode(errorCode))
        problemDetail.title = errorCode.message
        problemDetail.detail = ex.bindingResult.fieldErrors.joinToString(", ") { it.defaultMessage ?: "" }
        problemDetail.setProperty("errorCode", errorCode.code)
        setCommonProblemProperties(problemDetail, request)
        return problemDetail
    }

    @ExceptionHandler(HttpMessageNotReadableException::class)
    fun handleHttpMessageNotReadableException(
        ex: HttpMessageNotReadableException,
        request: HttpServletRequest,
    ): ProblemDetail {
        val errorCode = CommonSystemErrorCode.INVALID_REQUEST
        log.error(ex) { "Invalid request, errorCode=${errorCode.code}" }
        val problemDetail = ProblemDetail.forStatus(determineHttpStatusFromErrorCode(errorCode))
        problemDetail.title = errorCode.message
        problemDetail.detail = ex.message ?: "Invalid request body"
        problemDetail.setProperty("errorCode", errorCode.code)
        setCommonProblemProperties(problemDetail, request)
        return problemDetail
    }

    @ExceptionHandler(MethodArgumentTypeMismatchException::class)
    fun handleMethodArgumentTypeMismatchException(
        ex: MethodArgumentTypeMismatchException,
        request: HttpServletRequest,
    ): ProblemDetail {
        val errorCode = CommonSystemErrorCode.INVALID_REQUEST
        log.error(ex) { "Invalid argument type, errorCode=${errorCode.code}" }
        val problemDetail = ProblemDetail.forStatus(determineHttpStatusFromErrorCode(errorCode))
        problemDetail.title = errorCode.message
        problemDetail.detail = "Parameter '${ex.name}' has invalid value: '${ex.value}'"
        problemDetail.setProperty("errorCode", errorCode.code)
        setCommonProblemProperties(problemDetail, request)
        return problemDetail
    }

    @ExceptionHandler(DomainException::class)
    fun handleDomainException(
        ex: DomainException,
        request: HttpServletRequest,
    ): ProblemDetail {
        val errorCode = ex.errorCode
        log.error(ex) { "Domain exception occurred, errorCode=${errorCode.code}" }
        val problemDetail = ProblemDetail.forStatus(determineHttpStatusFromErrorCode(errorCode))
        problemDetail.title = errorCode.message
        problemDetail.detail = ex.message ?: ""
        problemDetail.setProperty("errorCode", errorCode.code)
        setCommonProblemProperties(problemDetail, request)
        return problemDetail
    }

    @ExceptionHandler(ApplicationException::class)
    fun handleApplicationException(
        ex: ApplicationException,
        request: HttpServletRequest,
    ): ProblemDetail {
        val errorCode = ex.errorCode
        log.error(ex) { "Application exception occurred, errorCode=${errorCode.code}" }
        val problemDetail = ProblemDetail.forStatus(determineHttpStatusFromErrorCode(errorCode))
        problemDetail.title = errorCode.message
        problemDetail.detail = ex.message ?: ""
        problemDetail.setProperty("errorCode", errorCode.code)
        setCommonProblemProperties(problemDetail, request)
        return problemDetail
    }

    @ExceptionHandler(OptimisticLockException::class)
    fun handleOptimisticLockException(
        ex: OptimisticLockException,
        request: HttpServletRequest,
    ): ProblemDetail {
        val errorCode = CommonSystemErrorCode.OPTIMISTIC_LOCK_ERROR
        log.error(ex) { "Optimistic lock exception occurred, errorCode=${errorCode.code}" }
        val problemDetail = ProblemDetail.forStatus(HttpStatus.CONFLICT)
        problemDetail.title = "Optimistic Lock Error"
        problemDetail.detail = "The resource you are trying to update has been modified by another transaction. Please try again."
        problemDetail.setProperty("errorCode", errorCode.code)
        setCommonProblemProperties(problemDetail, request)
        return problemDetail
    }

    @ExceptionHandler(IllegalArgumentException::class)
    fun handleIllegalArgumentException(
        ex: IllegalArgumentException,
        request: HttpServletRequest,
    ): ProblemDetail {
        val errorCode = CommonSystemErrorCode.INVALID_REQUEST
        log.error(ex) { "Illegal argument exception occurred, errorCode=${errorCode.code}" }
        val problemDetail = ProblemDetail.forStatus(HttpStatus.BAD_REQUEST)
        problemDetail.title = "Bad Request"
        problemDetail.detail = ex.message ?: "Invalid argument provided."
        problemDetail.setProperty("errorCode", errorCode.code)
        setCommonProblemProperties(problemDetail, request)
        return problemDetail
    }

    @ExceptionHandler(OutboxException::class)
    fun handleOutboxException(
        ex: OutboxException,
        request: HttpServletRequest,
    ): ProblemDetail {
        log.error(ex) { "Outbox exception occurred, errorCode=${ex.errorCode.code}" }
        val httpStatus = determineHttpStatusFromOutboxErrorCode(ex.errorCode)
        val problemDetail = ProblemDetail.forStatus(httpStatus)
        problemDetail.title = ex.errorCode.message
        problemDetail.detail = ex.message ?: ""
        problemDetail.setProperty("errorCode", ex.errorCode.code)
        setCommonProblemProperties(problemDetail, request)
        return problemDetail
    }

    private fun determineHttpStatusFromOutboxErrorCode(errorCode: OutboxErrorCodes): HttpStatus =
        when (errorCode) {
            OutboxErrorCodes.MESSAGE_NOT_FOUND -> HttpStatus.NOT_FOUND
            OutboxErrorCodes.KAFKA_SEND_FAILED,
            OutboxErrorCodes.MESSAGE_PROCESSING_FAILED,
            OutboxErrorCodes.DATABASE_ERROR,
            OutboxErrorCodes.SERIALIZATION_ERROR,
            OutboxErrorCodes.DATABASE_OPERATION_FAILED,
            OutboxErrorCodes.UNEXPECTED_INFRA_ERROR,
            -> HttpStatus.INTERNAL_SERVER_ERROR
            OutboxErrorCodes.MAX_RETRIES_EXCEEDED -> HttpStatus.SERVICE_UNAVAILABLE
            OutboxErrorCodes.INVALID_MESSAGE_STATUS -> HttpStatus.BAD_REQUEST
        }

    @ExceptionHandler(Exception::class)
    fun handleAllExceptions(
        ex: Exception,
        request: HttpServletRequest,
    ): ProblemDetail {
        val errorCode = CommonSystemErrorCode.INTERNAL_SERVER_ERROR
        log.error(ex) { "An unexpected error occurred, errorCode=${errorCode.code}" }
        val problemDetail = ProblemDetail.forStatus(HttpStatus.INTERNAL_SERVER_ERROR)
        problemDetail.title = "Internal Server Error"
        problemDetail.detail = "An unexpected error occurred. Please try again later."
        problemDetail.setProperty("errorCode", errorCode.code)
        setCommonProblemProperties(problemDetail, request)
        return problemDetail
    }

    private fun determineHttpStatusFromErrorCode(errorCode: ErrorCode): HttpStatus =
        when (errorCode) {
            is CommonSystemErrorCode ->
                when (errorCode) {
                    CommonSystemErrorCode.VALIDATION_ERROR, CommonSystemErrorCode.INVALID_REQUEST -> HttpStatus.BAD_REQUEST
                    CommonSystemErrorCode.RESOURCE_NOT_FOUND -> HttpStatus.NOT_FOUND
                    CommonSystemErrorCode.UNAUTHORIZED -> HttpStatus.UNAUTHORIZED
                    CommonSystemErrorCode.FORBIDDEN -> HttpStatus.FORBIDDEN
                    CommonSystemErrorCode.CONFLICT, CommonSystemErrorCode.OPTIMISTIC_LOCK_ERROR -> HttpStatus.CONFLICT
                    CommonSystemErrorCode.TOO_MANY_REQUESTS -> HttpStatus.TOO_MANY_REQUESTS
                    CommonSystemErrorCode.SERVICE_UNAVAILABLE -> HttpStatus.SERVICE_UNAVAILABLE
                    else -> HttpStatus.INTERNAL_SERVER_ERROR
                }

            else -> HttpStatus.INTERNAL_SERVER_ERROR
        }

    private fun setCommonProblemProperties(
        problemDetail: ProblemDetail,
        request: HttpServletRequest,
    ) {
        problemDetail.type = URI.create("https://errors.restaurant.com/${problemDetail.properties?.get("errorCode")}")
        problemDetail.instance = URI.create(request.requestURI)
        problemDetail.setProperty("timestamp", Instant.now())
    }
}
