package com.restaurant.presentation.account.exception

import com.restaurant.domain.account.exception.AccountNotFoundException
import com.restaurant.domain.account.exception.InsufficientBalanceException
import jakarta.validation.ConstraintViolationException
import org.springframework.http.HttpStatus
import org.springframework.http.ProblemDetail
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.MethodArgumentNotValidException
import org.springframework.web.bind.annotation.ControllerAdvice
import org.springframework.web.bind.annotation.ExceptionHandler
import java.net.URI
import java.time.Instant

/**
 * 계좌 관련 전역 예외 처리기
 * RFC 7807 형식(ProblemDetail)으로 에러 응답을 변환
 */
@ControllerAdvice(basePackages = ["com.restaurant.presentation.account"])
class AccountGlobalExceptionHandler {
    /**
     * 계좌를 찾을 수 없는 예외 처리
     */
    @ExceptionHandler(AccountNotFoundException::class)
    fun handleAccountNotFoundException(ex: AccountNotFoundException): ResponseEntity<ProblemDetail> {
        val problem =
            ProblemDetail.forStatus(HttpStatus.NOT_FOUND).apply {
                type = URI.create("https://example.com/probs/account_not_found")
                title = "Account Not Found"
                detail = ex.message
                setProperty("errorCode", "ACCOUNT_NOT_FOUND")
                setProperty("timestamp", Instant.now().toString())
            }
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(problem)
    }

    /**
     * 잔액 부족 예외 처리
     */
    @ExceptionHandler(InsufficientBalanceException::class)
    fun handleInsufficientBalanceException(ex: InsufficientBalanceException): ResponseEntity<ProblemDetail> {
        val problem =
            ProblemDetail.forStatus(HttpStatus.BAD_REQUEST).apply {
                type = URI.create("https://example.com/probs/insufficient_balance")
                title = "Insufficient Balance"
                detail = ex.message
                setProperty("errorCode", "INSUFFICIENT_BALANCE")
                setProperty("timestamp", Instant.now().toString())
                setProperty("currentBalance", ex.currentBalance.value)
                setProperty("requiredAmount", ex.requiredAmount.value)
            }
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(problem)
    }

    /**
     * 유효성 검사 실패 예외 처리
     */
    @ExceptionHandler(MethodArgumentNotValidException::class)
    fun handleValidationException(ex: MethodArgumentNotValidException): ResponseEntity<ProblemDetail> {
        val fieldErrors =
            ex.bindingResult.fieldErrors.map {
                mapOf(
                    "field" to it.field,
                    "message" to (it.defaultMessage ?: "Invalid value"),
                    "rejectedValue" to it.rejectedValue,
                )
            }

        val problem =
            ProblemDetail.forStatus(HttpStatus.BAD_REQUEST).apply {
                type = URI.create("https://example.com/probs/validation_error")
                title = "Validation Error"
                detail = "입력값 유효성 검사에 실패했습니다."
                setProperty("errorCode", "VALIDATION_ERROR")
                setProperty("timestamp", Instant.now().toString())
                setProperty("errors", fieldErrors)
            }
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(problem)
    }

    /**
     * 제약 조건 위반 예외 처리
     */
    @ExceptionHandler(ConstraintViolationException::class)
    fun handleConstraintViolationException(ex: ConstraintViolationException): ResponseEntity<ProblemDetail> {
        val violations =
            ex.constraintViolations.map {
                mapOf(
                    "property" to it.propertyPath.toString(),
                    "message" to it.message,
                    "invalidValue" to it.invalidValue,
                )
            }

        val problem =
            ProblemDetail.forStatus(HttpStatus.BAD_REQUEST).apply {
                type = URI.create("https://example.com/probs/constraint_violation")
                title = "Constraint Violation"
                detail = "제약 조건 위반이 발생했습니다."
                setProperty("errorCode", "CONSTRAINT_VIOLATION")
                setProperty("timestamp", Instant.now().toString())
                setProperty("violations", violations)
            }
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(problem)
    }

    /**
     * 기타 모든 예외에 대한 처리
     */
    @ExceptionHandler(Exception::class)
    fun handleGenericException(ex: Exception): ResponseEntity<ProblemDetail> {
        val problem =
            ProblemDetail.forStatus(HttpStatus.INTERNAL_SERVER_ERROR).apply {
                type = URI.create("https://example.com/probs/internal_server_error")
                title = "Internal Server Error"
                detail = "서버 내부 오류가 발생했습니다."
                setProperty("errorCode", "INTERNAL_SERVER_ERROR")
                setProperty("timestamp", Instant.now().toString())
                setProperty("exception", ex.javaClass.simpleName)
            }
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(problem)
    }
}
