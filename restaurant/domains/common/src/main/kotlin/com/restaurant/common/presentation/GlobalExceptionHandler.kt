package com.restaurant.common.presentation

import com.restaurant.common.core.exception.ApplicationException
import com.restaurant.common.core.exception.DomainException
import com.restaurant.common.core.exception.InfrastructureException
import com.restaurant.common.core.exception.PresentationException
import jakarta.validation.ConstraintViolationException
import org.springframework.http.HttpStatus
import org.springframework.http.ProblemDetail
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.MethodArgumentNotValidException
import org.springframework.web.bind.annotation.ControllerAdvice
import org.springframework.web.bind.annotation.ExceptionHandler
import java.net.URI
import java.time.Instant

@ControllerAdvice
class GlobalExceptionHandler {
  @ExceptionHandler(DomainException::class)
  fun handleDomainException(ex: DomainException): ResponseEntity<ProblemDetail> {
    val problem =
      ProblemDetail.forStatus(HttpStatus.BAD_REQUEST).apply {
        type = URI.create("https://example.com/probs/domain-error")
        title = "도메인 오류"
        detail = ex.message
        setProperty("errorCode", "DOMAIN_ERROR")
        setProperty("timestamp", Instant.now().toString())
      }
    return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(problem)
  }

  @ExceptionHandler(ApplicationException::class)
  fun handleApplicationException(ex: ApplicationException): ResponseEntity<ProblemDetail> {
    val problem =
      ProblemDetail.forStatus(HttpStatus.BAD_REQUEST).apply {
        type = URI.create("https://example.com/probs/application-error")
        title = "어플리케이션 오류"
        detail = ex.message
        setProperty("errorCode", "APPLICATION_ERROR")
        setProperty("timestamp", Instant.now().toString())
      }
    return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(problem)
  }

  @ExceptionHandler(InfrastructureException::class)
  fun handleInfrastructureException(ex: InfrastructureException): ResponseEntity<ProblemDetail> {
    val problem =
      ProblemDetail.forStatus(HttpStatus.INTERNAL_SERVER_ERROR).apply {
        type = URI.create("https://example.com/probs/infrastructure-error")
        title = "인프라스트럭처 오류"
        detail = ex.message
        setProperty("errorCode", "INFRASTRUCTURE_ERROR")
        setProperty("timestamp", Instant.now().toString())
      }
    return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(problem)
  }

  @ExceptionHandler(PresentationException::class)
  fun handlePresentationException(ex: PresentationException): ResponseEntity<ProblemDetail> {
    val problem =
      ProblemDetail.forStatus(HttpStatus.BAD_REQUEST).apply {
        type = URI.create("https://example.com/probs/presentation-error")
        title = "프레젠테이션 오류"
        detail = ex.message
        setProperty("errorCode", "PRESENTATION_ERROR")
        setProperty("timestamp", Instant.now().toString())
      }
    return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(problem)
  }

  @ExceptionHandler(MethodArgumentNotValidException::class)
  fun handleValidationException(ex: MethodArgumentNotValidException): ResponseEntity<ProblemDetail> {
    val invalidParams =
      ex.bindingResult.fieldErrors.map {
        mapOf("field" to it.field, "message" to (it.defaultMessage ?: "유효하지 않은 값입니다."))
      }

    val problem =
      ProblemDetail.forStatus(HttpStatus.BAD_REQUEST).apply {
        type = URI.create("https://example.com/probs/invalid-params")
        title = "유효성 검사 오류"
        detail = "입력 데이터가 유효하지 않습니다."
        setProperty("errorCode", "VALIDATION_ERROR")
        setProperty("timestamp", Instant.now().toString())
        setProperty("invalid-params", invalidParams)
      }
    return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(problem)
  }

  @ExceptionHandler(ConstraintViolationException::class)
  fun handleConstraintViolationException(ex: ConstraintViolationException): ResponseEntity<ProblemDetail> {
    val invalidParams =
      ex.constraintViolations.map {
        mapOf("field" to it.propertyPath.toString(), "message" to it.message)
      }

    val problem =
      ProblemDetail.forStatus(HttpStatus.BAD_REQUEST).apply {
        type = URI.create("https://example.com/probs/invalid-params")
        title = "유효성 검사 오류"
        detail = "입력 데이터가 유효하지 않습니다."
        setProperty("errorCode", "VALIDATION_ERROR")
        setProperty("timestamp", Instant.now().toString())
        setProperty("invalid-params", invalidParams)
      }
    return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(problem)
  }

  @ExceptionHandler(Exception::class)
  fun handleGenericException(ex: Exception): ResponseEntity<ProblemDetail> {
    val problem =
      ProblemDetail.forStatus(HttpStatus.INTERNAL_SERVER_ERROR).apply {
        type = URI.create("https://example.com/probs/server-error")
        title = "서버 오류"
        detail = "서버에서 예상치 못한 오류가 발생했습니다."
        setProperty("errorCode", "SYSTEM_ERROR")
        setProperty("timestamp", Instant.now().toString())
      }
    return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(problem)
  }
}
