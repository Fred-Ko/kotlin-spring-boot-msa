package com.ddd.user.presentation.global

import com.ddd.support.exception.ApplicationException
import com.ddd.user.application.exception.UserApplicationException
import jakarta.validation.ConstraintViolationException
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.MethodArgumentNotValidException
import org.springframework.web.bind.annotation.ExceptionHandler
import org.springframework.web.bind.annotation.RestControllerAdvice

@RestControllerAdvice
class GlobalExceptionHandler {
    @ExceptionHandler(UserApplicationException.UserNotFound::class)
    fun handleUserNotFoundException(
            exception: UserApplicationException.UserNotFound,
    ): ResponseEntity<ErrorResponse> {
        val errorResponse = ErrorResponse(message = exception.message)
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(errorResponse)
    }

    @ExceptionHandler(UserApplicationException.EmailAlreadyExists::class)
    fun handleEmailAlreadyExistsException(
            exception: UserApplicationException.EmailAlreadyExists,
    ): ResponseEntity<ErrorResponse> {
        val errorResponse = ErrorResponse(message = exception.message)
        return ResponseEntity.status(HttpStatus.CONFLICT).body(errorResponse)
    }

    @ExceptionHandler(UserApplicationException.UserCreationFailed::class)
    fun handleUserCreationFailedException(
            exception: UserApplicationException.UserCreationFailed,
    ): ResponseEntity<ErrorResponse> {
        val errorResponse = ErrorResponse(message = exception.message)
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(errorResponse)
    }

    @ExceptionHandler(UserApplicationException.DeleteUserFailed::class)
    fun handleDeleteUserFailedException(
            exception: UserApplicationException.DeleteUserFailed,
    ): ResponseEntity<ErrorResponse> {
        val errorResponse = ErrorResponse(message = exception.message)
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(errorResponse)
    }

    @ExceptionHandler(UserApplicationException.UpdateUserFailed::class)
    fun handleUpdateUserFailedException(
            exception: UserApplicationException.UpdateUserFailed,
    ): ResponseEntity<ErrorResponse> {
        val errorResponse = ErrorResponse(message = exception.message)
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(errorResponse)
    }

    @ExceptionHandler(UserApplicationException.UserAlreadyExists::class)
    fun handleUserAlreadyExistsException(
            exception: UserApplicationException.UserAlreadyExists,
    ): ResponseEntity<ErrorResponse> {
        val errorResponse = ErrorResponse(message = exception.message)
        return ResponseEntity.status(HttpStatus.CONFLICT).body(errorResponse)
    }

    @ExceptionHandler(UserApplicationException.GetUsersFailed::class)
    fun handleGetUsersFailedException(
            exception: UserApplicationException.GetUsersFailed,
    ): ResponseEntity<ErrorResponse> {
        val errorResponse = ErrorResponse(message = exception.message)
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(errorResponse)
    }

    @ExceptionHandler(UserApplicationException.GetUserFailed::class)
    fun handleGetUserFailedException(
            exception: UserApplicationException.GetUserFailed,
    ): ResponseEntity<ErrorResponse> {
        val errorResponse = ErrorResponse(message = exception.message)
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(errorResponse)
    }

    @ExceptionHandler(ApplicationException::class)
    fun handleApplicationException(exception: ApplicationException): ResponseEntity<ErrorResponse> {
        val errorResponse = ErrorResponse(message = exception.message ?: "Invalid request")
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(errorResponse)
    }

    @ExceptionHandler(MethodArgumentNotValidException::class)
    fun handleMethodArgumentNotValidException(
            exception: MethodArgumentNotValidException,
    ): ResponseEntity<ErrorResponse> {
        val errorResponse =
                ErrorResponse(
                        message = exception.bindingResult.allErrors.first().defaultMessage
                                        ?: "Invalid request"
                )
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(errorResponse)
    }

    @ExceptionHandler(ConstraintViolationException::class)
    fun handleConstraintViolationException(
            exception: ConstraintViolationException,
    ): ResponseEntity<ErrorResponse> {
        val errorResponse =
                ErrorResponse(
                        message = exception.constraintViolations.first().message
                                        ?: "Invalid request"
                )
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(errorResponse)
    }

    data class ErrorResponse(val message: String)
}
