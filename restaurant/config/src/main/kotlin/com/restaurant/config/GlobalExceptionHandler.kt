package com.restaurant.config

import com.restaurant.common.core.error.CommonSystemErrorCode
import com.restaurant.common.core.exception.ApplicationException
import com.restaurant.common.core.exception.DomainException
import com.restaurant.config.filter.CorrelationIdFilter
import com.restaurant.outbox.infrastructure.exception.OutboxException // Import Outbox Exception
import jakarta.persistence.OptimisticLockException // Import OptimisticLockException
import jakarta.validation.ConstraintViolationException
import io.github.oshai.kotlinlogging.KotlinLogging
import org.slf4j.MDC
import org.springframework.http.HttpHeaders
import org.springframework.http.HttpStatus
import org.springframework.http.HttpStatusCode
import org.springframework.http.ProblemDetail
import org.springframework.http.ResponseEntity
import org.springframework.http.converter.HttpMessageNotReadableException
import org.springframework.web.bind.MethodArgumentNotValidException
import org.springframework.web.bind.annotation.ExceptionHandler
import org.springframework.web.bind.annotation.RestControllerAdvice
import org.springframework.web.context.request.WebRequest
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler
import java.net.URI
import java.time.Instant

private val log = KotlinLogging.logger {}

@RestControllerAdvice
class GlobalExceptionHandler : ResponseEntityExceptionHandler() { // Extend for Spring MVC default handling

    private val baseUri = "/errors"

    // Rule 51: Handle DomainException
    @ExceptionHandler(DomainException::class)
    fun handleDomainException(
        ex: DomainException,
        request: WebRequest,
    ): ResponseEntity<ProblemDetail> {
        log.warn(ex) { "Domain Exception: code=${ex.errorCode.code}, message=${ex.message}" }
        val status = determineHttpStatus(ex.errorCode.code) // Rule 73: Determine status based on code
        val problemDetail = createProblemDetail(ex, status, ex.message ?: "Domain rule violation", ex.errorCode.code, request)
        // Rule 73: Handle Validation specifics
        if (ex is DomainException.Validation) {
            // Create invalid-params structure if needed, depends on Validation exception details
            // Example: problemDetail.setProperty("invalid-params", listOf(mapOf("field" to ex.field, "reason" to ex.reason)))
        }
        return ResponseEntity.status(status).body(problemDetail)
    }

    // Rule 51: Handle ApplicationException
    @ExceptionHandler(ApplicationException::class)
    fun handleApplicationException(
        ex: ApplicationException,
        request: WebRequest,
    ): ResponseEntity<ProblemDetail> {
        log.warn(ex) { "Application Exception: code=${ex.errorCode.code}, message=${ex.message}" }
        val status = determineHttpStatus(ex.errorCode.code)
        val problemDetail = createProblemDetail(ex, status, ex.message ?: "Application error occurred", ex.errorCode.code, request)
        return ResponseEntity.status(status).body(problemDetail)
    }

    // Rule 51 & 73: Handle OutboxException (from independent module)
    @ExceptionHandler(OutboxException::class)
    fun handleOutboxException(
        ex: OutboxException,
        request: WebRequest,
    ): ResponseEntity<ProblemDetail> {
        log.error(ex) { "Outbox Exception: code=${ex.code}, message=${ex.message}" } // Use ex.code directly
        // Determine HttpStatus based on Outbox specific codes if needed, default to 500
        val status = determineHttpStatus(ex.code, defaultStatus = HttpStatus.INTERNAL_SERVER_ERROR)
        val problemDetail = createProblemDetail(ex, status, ex.message ?: "Outbox operation failed", ex.code, request)
        return ResponseEntity.status(status).body(problemDetail)
    }

    // Rule 51 & 73: Handle OptimisticLockException
    @ExceptionHandler(OptimisticLockException::class)
    fun handleOptimisticLockException(
        ex: OptimisticLockException,
        request: WebRequest,
    ): ResponseEntity<ProblemDetail> {
        log.warn(ex) { "Optimistic Lock Exception: ${ex.message}" }
        val status = HttpStatus.CONFLICT // Standard status for optimistic locking failure
        val problemDetail = createProblemDetail(ex, status, "Data conflict detected. Please refresh and try again.", "COMMON-SYSTEM-409", request)
        return ResponseEntity.status(status).body(problemDetail)
    }

    // Rule 51: Handle ConstraintViolationException (e.g., @Validated on service parameters)
    @ExceptionHandler(ConstraintViolationException::class)
    fun handleConstraintViolationException(
        ex: ConstraintViolationException,
        request: WebRequest,
    ): ResponseEntity<ProblemDetail> {
        log.warn(ex) { "Constraint Violation: ${ex.message}" }
        val status = HttpStatus.BAD_REQUEST
        val problemDetail = createProblemDetail(ex, status, "Input validation failed", "COMMON-SYSTEM-400", request)
        val invalidParams = ex.constraintViolations.map {
            mapOf("field" to it.propertyPath.toString(), "reason" to it.message)
        }
        problemDetail.setProperty("invalid-params", invalidParams)
        return ResponseEntity.status(status).body(problemDetail)
    }

    // Override for MethodArgumentNotValidException (@Valid on Controller DTOs) - Rule 48, 51
    override fun handleMethodArgumentNotValid(
        ex: MethodArgumentNotValidException,
        headers: HttpHeaders,
        status: HttpStatusCode,
        request: WebRequest,
    ): ResponseEntity<Any>? { // Return type is nullable Any?
        log.warn(ex) { "Method Argument Not Valid: ${ex.message}" }
        val problemDetail = createProblemDetail(ex, status, "Validation failed for request body/parameters", "COMMON-SYSTEM-400", request)
        val invalidParams = ex.bindingResult.fieldErrors.map {
            mapOf("field" to it.field, "reason" to (it.defaultMessage ?: "Invalid value"))
        }
        problemDetail.setProperty("invalid-params", invalidParams)
        return ResponseEntity.status(status).body(problemDetail)
    }

    // Override for HttpMessageNotReadableException (Invalid JSON etc.) - Rule 51
    override fun handleHttpMessageNotReadable(
        ex: HttpMessageNotReadableException,
        headers: HttpHeaders,
        status: HttpStatusCode,
        request: WebRequest,
    ): ResponseEntity<Any>? {
        log.warn(ex) { "HTTP Message Not Readable: ${ex.message}" }
        val problemDetail = createProblemDetail(ex, status, "Failed to read request body", "COMMON-SYSTEM-400", request)
        // Add more specific details if possible from ex.cause
        return ResponseEntity.status(status).body(problemDetail)
    }

    // Rule 51: Fallback for any other Exception
    @ExceptionHandler(Exception::class)
    fun handleGenericException(
        ex: Exception,
        request: WebRequest,
    ): ResponseEntity<ProblemDetail> {
        log.error(ex) { "Unexpected Exception: ${ex.message}" }
        val status = HttpStatus.INTERNAL_SERVER_ERROR
        val problemDetail = createProblemDetail(ex, status, "An unexpected internal error occurred", "COMMON-SYSTEM-500", request)
        return ResponseEntity.status(status).body(problemDetail)
    }

    // --- Helper Methods --- 

    // Rule 73: Determine HttpStatus based on ErrorCode string
    private fun determineHttpStatus(errorCode: String?, defaultStatus: HttpStatus = HttpStatus.BAD_REQUEST): HttpStatus {
        // Implement mapping logic based on errorCode prefixes or specific codes
        return when {
            errorCode == null -> defaultStatus
            // Domain/App Validation Errors
            errorCode.startsWith("USER-DOMAIN-") && errorCode.contains("VALIDATION") -> HttpStatus.BAD_REQUEST
            errorCode.startsWith("ORDER-DOMAIN-") && errorCode.contains("VALIDATION") -> HttpStatus.BAD_REQUEST // Example for another domain
            // Specific Domain Errors
            errorCode == "USER-DOMAIN-001" /* DuplicateUsername */ -> HttpStatus.CONFLICT
            errorCode == "USER-DOMAIN-002" /* DuplicateEmail */ -> HttpStatus.CONFLICT
            errorCode == "USER-DOMAIN-006" /* PasswordMismatch */ -> HttpStatus.UNAUTHORIZED // Or BAD_REQUEST
            errorCode == "USER-DOMAIN-015" /* AlreadyWithdrawn */ -> HttpStatus.GONE
            errorCode == "ORDER-DOMAIN-002" /* InsufficientBalance */ -> HttpStatus.BAD_REQUEST // Example for another domain
            // Application Errors
            errorCode == "USER-APP-101" /* UserNotFound */ -> HttpStatus.NOT_FOUND
            errorCode == "USER-APP-102" /* InvalidCredentials */ -> HttpStatus.UNAUTHORIZED
            errorCode == "USER-APP-103" /* UserInactive */ -> HttpStatus.FORBIDDEN
            errorCode == "PAYMENT-APP-201" /* ExternalApiFailure */ -> HttpStatus.SERVICE_UNAVAILABLE // Example
            // Outbox Errors (Using defaultStatus unless specific mapping needed)
            errorCode.startsWith("OUTBOX-INFRA-") -> defaultStatus
            // Common System Errors (already handled specifically, but as example)
            errorCode == "COMMON-SYSTEM-400" -> HttpStatus.BAD_REQUEST
            errorCode == "COMMON-SYSTEM-401" -> HttpStatus.UNAUTHORIZED
            errorCode == "COMMON-SYSTEM-403" -> HttpStatus.FORBIDDEN
            errorCode == "COMMON-SYSTEM-404" -> HttpStatus.NOT_FOUND
            errorCode == "COMMON-SYSTEM-409" -> HttpStatus.CONFLICT
            errorCode == "COMMON-SYSTEM-500" -> HttpStatus.INTERNAL_SERVER_ERROR
            else -> defaultStatus // Default for unmapped codes
        }
    }

    // Rule 50: Create ProblemDetail object
    private fun createProblemDetail(ex: Exception, status: HttpStatusCode, detail: String, errorCode: String?, request: WebRequest): ProblemDetail {
        val problemDetail = ProblemDetail.forStatusAndDetail(status, detail)
        problemDetail.title = status.reasonPhrase // Default title from status
        problemDetail.type = URI.create("$baseUri/${errorCode ?: "unknown"}") // Link to error documentation
        problemDetail.setProperty("timestamp", Instant.now())
        problemDetail.setProperty("errorCode", errorCode ?: "UNKNOWN")
        // Rule 49, 50, 51: Include correlationId from MDC
        problemDetail.setProperty("correlationId", MDC.get(CorrelationIdFilter.CORRELATION_ID_MDC_KEY) ?: "N/A")
        // Optionally add instance
        // problemDetail.instance = URI.create(request.getDescription(false).substringAfter("uri="))
        return problemDetail
    }
}
