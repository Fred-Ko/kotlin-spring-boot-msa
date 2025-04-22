package com.restaurant.common.presentation

// KtLint: import 순서 조정 및 error 패키지 사용
import com.restaurant.common.core.error.CommonSystemErrorCode // OptimisticLockException 처리에 사용
import com.restaurant.common.core.exception.ApplicationException
import com.restaurant.common.core.exception.DomainException
import com.restaurant.common.core.exception.InfrastructureException
import com.restaurant.common.core.exception.PresentationException
import com.restaurant.domain.account.error.AccountDomainException // exception -> error
import com.restaurant.domain.user.error.UserDomainException // exception -> error
import com.restaurant.independent.outbox.infrastructure.error.OutboxException
import jakarta.persistence.OptimisticLockException
import jakarta.validation.ConstraintViolationException
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

@ControllerAdvice
class GlobalExceptionHandler(
    @Value("\${app.problem.base-url:https://api.restaurant.com/problems}") private val problemBaseUrl: String,
) {
    private val log = LoggerFactory.getLogger(GlobalExceptionHandler::class.java)

    /**
     * MDC(Mapped Diagnostic Context)에서 correlationId를 가져옵니다.
     * correlationId가 없는 경우 새로 생성합니다.
     */
    // KtLint: 함수 본문을 표현식으로 변경
    private fun getCorrelationIdFromMDC(): String =
        MDC.get("correlationId") ?: UUID.randomUUID().toString()

    /**
     * 에러 코드에 따른 HTTP 상태 코드 매핑
     */
    private fun determineHttpStatusFromCode(code: String): HttpStatus =
        when {
            // Account Domain Error Codes
            code == "ACCOUNT-DOMAIN-001" -> HttpStatus.NOT_FOUND // Account not found
            code == "ACCOUNT-DOMAIN-002" -> HttpStatus.BAD_REQUEST // Insufficient balance

            // Account Application Error Codes
            code == "ACCOUNT-APPLICATION-001" -> HttpStatus.NOT_FOUND // Account not found
            // KtLint: 최대 줄 길이 초과 수정 (주석 분리)
            code == "ACCOUNT-APPLICATION-002" -> HttpStatus.BAD_REQUEST // Insufficient balance
                                                                        // (if ApplicationException wraps this, although Rule 70 limits ApplicationException)
            code == "ACCOUNT-APPLICATION-003" -> HttpStatus.NOT_FOUND // Transaction not found
            code == "ACCOUNT-APPLICATION-004" -> HttpStatus.BAD_REQUEST // Transaction already cancelled
            code == "ACCOUNT-APPLICATION-999" -> HttpStatus.INTERNAL_SERVER_ERROR // System error

            // User Domain Error Codes
            code == "USER-DOMAIN-001" -> HttpStatus.NOT_FOUND // User not found
            code == "USER-DOMAIN-002" -> HttpStatus.CONFLICT // Duplicate email
            code == "USER-DOMAIN-003" -> HttpStatus.BAD_REQUEST // Password mismatch
            code == "USER-DOMAIN-004" -> HttpStatus.NOT_FOUND // Address not found
            code == "USER-DOMAIN-005" -> HttpStatus.BAD_REQUEST // Max address limit
            code == "USER-DOMAIN-006" -> HttpStatus.BAD_REQUEST // Default address cannot be removed
            code == "USER-DOMAIN-007" -> HttpStatus.BAD_REQUEST // Cannot remove last address
            code == "USER-DOMAIN-008" -> HttpStatus.BAD_REQUEST // Invalid password format
            code == "USER-DOMAIN-009" -> HttpStatus.BAD_REQUEST // Invalid input (general domain validation)

            // User Application Error Codes
            code == "USER-APPLICATION-001" -> HttpStatus.BAD_REQUEST // Invalid input (application level validation)
            code == "USER-APPLICATION-002" -> HttpStatus.UNAUTHORIZED // Authentication failed
            code == "USER-APPLICATION-003" -> HttpStatus.INTERNAL_SERVER_ERROR // External service error
            code == "USER-APPLICATION-999" -> HttpStatus.INTERNAL_SERVER_ERROR // System error

            // Outbox Error Codes (assuming OUTBOX-XXX maps to Internal Server Error unless specified)
            code.startsWith("OUTBOX-") -> HttpStatus.INTERNAL_SERVER_ERROR

            // 인증/인가 관련 에러
            code.startsWith("AUTH-") -> HttpStatus.UNAUTHORIZED
            code.contains("-AUTH-") -> HttpStatus.UNAUTHORIZED
            code.contains("-FORBIDDEN-") -> HttpStatus.FORBIDDEN

            // 리소스 찾을 수 없음
            code.contains("-NOT-FOUND") -> HttpStatus.NOT_FOUND
            code.endsWith("-404") -> HttpStatus.NOT_FOUND

            // 중복/충돌
            code.contains("-DUPLICATE-") -> HttpStatus.CONFLICT
            code.contains("-CONFLICT-") -> HttpStatus.CONFLICT
            code.endsWith("-409") -> HttpStatus.CONFLICT

            // 유효성 검증 실패
            code.contains("-INVALID-") -> HttpStatus.BAD_REQUEST
            code.contains("-VALIDATION-") -> HttpStatus.BAD_REQUEST
            code.endsWith("-400") -> HttpStatus.BAD_REQUEST

            // 비즈니스 규칙 위반
            code.startsWith("BIZ-") -> HttpStatus.UNPROCESSABLE_ENTITY
            code.contains("-RULE-") -> HttpStatus.UNPROCESSABLE_ENTITY
            code.endsWith("-422") -> HttpStatus.UNPROCESSABLE_ENTITY

            // 시스템/인프라 에러
            code.startsWith("SYS-") -> HttpStatus.INTERNAL_SERVER_ERROR
            code.contains("-SYSTEM-") -> HttpStatus.INTERNAL_SERVER_ERROR
            code.endsWith("-500") -> HttpStatus.INTERNAL_SERVER_ERROR

            // 기본값
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

    /**
     * 요청 헤더에서 correlationId를 가져옵니다.
     */
    // KtLint: 함수 본문을 표현식으로 변경
    private fun getCorrelationIdFromRequest(request: WebRequest): String? =
        request.getHeader("X-Correlation-Id")

    /**
     * 요청 헤더 또는 MDC에서 correlationId를 가져옵니다.
     */
    // KtLint: 함수 본문을 표현식으로 변경
    private fun getCorrelationId(request: WebRequest): String =
        getCorrelationIdFromRequest(request) ?: getCorrelationIdFromMDC()

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

        // AccountDomainException 상세 정보 추가 (AccountGlobalExceptionHandler에서 가져옴)
        when (ex) {
            is AccountDomainException.Account.InsufficientBalance -> {
                // KtLint: 들여쓰기 수정
                problem.setProperty("currentBalance", ex.currentBalance.value)
                problem.setProperty("requiredAmount", ex.requiredAmount.value)
            }
            // UserDomainException.Validation 예외는 별도 핸들러에서 처리
        }

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

    @ExceptionHandler(OptimisticLockException::class) // jakarta.persistence.OptimisticLockException
    fun handleOptimisticLockException(
        ex: OptimisticLockException,
        request: WebRequest,
    ): ResponseEntity<ProblemDetail> {
        val correlationId = getCorrelationId(request)
        val status = HttpStatus.CONFLICT // Rule 31, 73: OptimisticLockException maps to Conflict
        // Assuming a common concurrency error code exists or using a generic system error code
        // CommonSystemErrorCode 사용하도록 수정
        val code = CommonSystemErrorCode.CONCURRENCY_FAILURE.code
        val title = "Concurrency Conflict"
        val detail = "데이터 동시성 충돌이 발생했습니다. 다시 시도해주세요."

        log.warn(
            "Optimistic Lock Exception Handled: correlationId={}, code={}, status={}, message={}",
            correlationId,
            code,
            status.value(),
            ex.message ?: "No message", // null 가능성 처리
            ex,
        )

        val problem = createProblemDetail(status, code, title, detail, request)
        return ResponseEntity.status(status).body(problem)
    }

    @ExceptionHandler(OutboxException::class) // com.restaurant.independent.outbox.infrastructure.error.OutboxException
    fun handleOutboxException(
        ex: OutboxException,
        request: WebRequest,
    ): ResponseEntity<ProblemDetail> {
        val correlationId = getCorrelationId(request)
        // OutboxException은 errorCode를 직접 가지도록 수정했으므로 바로 접근
        val code = ex.errorCode.code
        val status = determineHttpStatusFromCode(code)
        val title = ex.errorCode.message
        val detail = ex.message // OutboxException 생성자에서 message를 non-null로 받음

        log.error(
            "Outbox Exception Handled: correlationId={}, code={}, status={}, message={}",
            correlationId,
            code,
            status.value(),
            ex.message, // null 가능성 없음
            ex,
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

    // Add handler for UserDomainException.Validation
    @ExceptionHandler(UserDomainException.Validation::class) // com.restaurant.domain.user.error.UserDomainException.Validation
    fun handleUserValidationException(
        ex: UserDomainException.Validation,
        request: WebRequest, // KtLint: 후행 쉼표 추가
    ): ResponseEntity<ProblemDetail> {
        val correlationId = getCorrelationId(request)
        // UserDomainException은 errorCode를 직접 가지므로 바로 접근
        val code = ex.errorCode.code
        val status = determineHttpStatusFromCode(code)
        val title = ex.errorCode.message
        val detail = ex.message // UserDomainException 생성자에서 message를 non-null로 받음

        log.warn(
            "User Domain Validation Exception Handled: correlationId={}, code={}, status={}, message={}",
            correlationId,
            code,
            status.value(),
            ex.message, // null 가능성 없음
            ex, // KtLint: 후행 쉼표 추가
        )

        val problem = createProblemDetail(status, code, title, detail, request)

        // Rule 48: 유효성 검사 실패 상세 정보를 invalid-params 필드에 포함
        when (ex) {
            is UserDomainException.Validation.InvalidPassword -> {
                // KtLint: 줄 바꿈 및 후행 쉼표 수정
                problem.setProperty(
                    "invalid-params",
                    listOf(
                        mapOf("field" to "password", "reason" to ex.message),
                    ),
                )
            }
            is UserDomainException.Validation.InvalidEmail -> {
                // KtLint: 줄 바꿈 및 후행 쉼표 수정
                problem.setProperty(
                    "invalid-params",
                    listOf(
                        mapOf("field" to "email", "reason" to ex.message),
                    ),
                )
            }
            // 다른 Validation 예외도 필요에 따라 처리
            else -> {
                // 일반적인 경우 메시지만 포함
                // KtLint: 줄 바꿈 및 후행 쉼표 수정
                problem.setProperty(
                    "invalid-params",
                    listOf(
                        mapOf("field" to "unknown", "reason" to ex.message),
                    ),
                )
            }
        }

        return ResponseEntity.status(status).body(problem)
    }
}
