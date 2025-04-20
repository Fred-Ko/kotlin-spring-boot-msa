package com.restaurant.common.presentation

import com.restaurant.common.core.exception.ApplicationException
import com.restaurant.common.core.exception.DomainException
import com.restaurant.common.core.exception.InfrastructureException
import com.restaurant.common.core.exception.PresentationException
import jakarta.validation.ConstraintViolationException
import org.slf4j.LoggerFactory
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

@ControllerAdvice
class GlobalExceptionHandler(
    @Value("\${app.problem.base-url:https://api.restaurant.com/problems}") private val problemBaseUrl: String,
) {
    private val log = LoggerFactory.getLogger(GlobalExceptionHandler::class.java)

    private fun createProblemDetail(
        status: HttpStatus,
        code: String,
        title: String,
        detail: String?,
        request: WebRequest,
    ): ProblemDetail {
        val correlationId = request.getHeader("X-Correlation-Id") ?: UUID.randomUUID().toString()
        return ProblemDetail.forStatus(status).apply {
            type = URI.create("$problemBaseUrl/${code.lowercase()}")
            this.title = title
            this.detail = detail ?: "오류가 발생했습니다."
            setProperty("errorCode", code)
            setProperty("timestamp", Instant.now().toString())
            setProperty("correlationId", correlationId)
        }
    }

    private fun getCorrelationId(request: WebRequest): String = request.getHeader("X-Correlation-Id") ?: "N/A"

    @ExceptionHandler(DomainException::class)
    fun handleDomainException(
        ex: DomainException,
        request: WebRequest,
    ): ResponseEntity<ProblemDetail> {
        val status = ex.errorCode.status
        val code = ex.errorCode.code
        val title = ex.errorCode.code

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
        val status = ex.errorCode.status
        val code = ex.errorCode.code
        val title = ex.errorCode.code

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

    @ExceptionHandler(InfrastructureException::class)
    fun handleInfrastructureException(
        ex: InfrastructureException,
        request: WebRequest,
    ): ResponseEntity<ProblemDetail> {
        val correlationId = getCorrelationId(request)
        val status = HttpStatus.INTERNAL_SERVER_ERROR
        val code = "INFRASTRUCTURE_ERROR"
        val title = "Infrastructure Error"
        val detail = ex.message ?: "인프라스트럭처 처리 중 오류가 발생했습니다."

        log.error(
            "Infrastructure Exception Handled: correlationId={}, code={}, status={}, message={}",
            correlationId,
            code,
            status.value(),
            ex.message,
            ex,
        )

        val problem = createProblemDetail(status, code, title, detail, request)
        return ResponseEntity.status(status).body(problem)
    }

    @ExceptionHandler(PresentationException::class)
    fun handlePresentationException(
        ex: PresentationException,
        request: WebRequest,
    ): ResponseEntity<ProblemDetail> {
        val correlationId = getCorrelationId(request)
        val status = HttpStatus.BAD_REQUEST
        val code = "PRESENTATION_ERROR"
        val title = "Presentation Error"
        val detail = ex.message ?: "프레젠테이션 처리 중 오류가 발생했습니다."

        log.warn(
            "Presentation Exception Handled: correlationId={}, code={}, status={}, message={}",
            correlationId,
            code,
            status.value(),
            ex.message,
            ex,
        )

        val problem = createProblemDetail(status, code, title, detail, request)
        return ResponseEntity.status(status).body(problem)
    }

    @ExceptionHandler(MethodArgumentNotValidException::class)
    fun handleValidationException(
        ex: MethodArgumentNotValidException,
        request: WebRequest,
    ): ResponseEntity<ProblemDetail> {
        val correlationId = getCorrelationId(request)
        val status = HttpStatus.BAD_REQUEST
        val code = "VALIDATION_ERROR"
        val title = "Validation Error"
        val detail = "입력 데이터가 유효하지 않습니다."

        val invalidParams =
            ex.bindingResult.fieldErrors.map {
                mapOf("field" to it.field, "reason" to (it.defaultMessage ?: "유효하지 않은 값입니다."))
            }
        log.warn(
            "Validation Exception Handled: correlationId={}, invalidParams={}\n{}",
            correlationId,
            invalidParams.toString(),
            ex.message,
        )

        val problem = createProblemDetail(status, code, title, detail, request)
        problem.setProperty("invalid-params", invalidParams)

        return ResponseEntity.status(status).body(problem)
    }

    @ExceptionHandler(ConstraintViolationException::class)
    fun handleConstraintViolationException(
        ex: ConstraintViolationException,
        request: WebRequest,
    ): ResponseEntity<ProblemDetail> {
        val correlationId = getCorrelationId(request)
        val status = HttpStatus.BAD_REQUEST
        val code = "VALIDATION_ERROR"
        val title = "Validation Error"
        val detail = "입력 데이터가 유효하지 않습니다."

        val invalidParams =
            ex.constraintViolations.map {
                mapOf("field" to it.propertyPath.toString(), "reason" to it.message)
            }
        log.warn(
            "Constraint Violation Handled: correlationId={}, invalidParams={}\n{}",
            correlationId,
            invalidParams,
            ex.message,
        )

        val problem = createProblemDetail(status, code, title, detail, request)
        problem.setProperty("invalid-params", invalidParams)

        return ResponseEntity.status(status).body(problem)
    }

    @ExceptionHandler(HttpMessageNotReadableException::class)
    fun handleMessageNotReadableException(
        ex: HttpMessageNotReadableException,
        request: WebRequest,
    ): ResponseEntity<ProblemDetail> {
        val correlationId = getCorrelationId(request)
        val status = HttpStatus.BAD_REQUEST
        val code = "MESSAGE_NOT_READABLE"
        val title = "Invalid Request Body"
        val detail = "요청 본문의 형식이 잘못되었거나 읽을 수 없습니다."
        log.warn(
            "Message Not Readable Exception Handled: correlationId={}, error={}",
            correlationId,
            ex.message,
        )

        val problem = createProblemDetail(status, code, title, detail, request)

        return ResponseEntity.status(status).body(problem)
    }

    @ExceptionHandler(Exception::class)
    fun handleGenericException(
        ex: Exception,
        request: WebRequest,
    ): ResponseEntity<ProblemDetail> {
        val correlationId = getCorrelationId(request)
        val status = HttpStatus.INTERNAL_SERVER_ERROR
        val code = "SYSTEM_ERROR"
        val title = "Internal Server Error"
        val detail = "서버에서 예상치 못한 오류가 발생했습니다."
        log.error(
            "Unhandled Exception: correlationId={}, error={}",
            correlationId,
            ex.message,
            ex,
        )

        val problem = createProblemDetail(status, code, title, detail, request)
        return ResponseEntity.status(status).body(problem)
    }
}
