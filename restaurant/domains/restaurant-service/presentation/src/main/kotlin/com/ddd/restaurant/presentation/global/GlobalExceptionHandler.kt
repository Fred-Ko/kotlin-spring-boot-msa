package com.ddd.restaurant.presentation.global

import com.ddd.restaurant.application.exception.RestaurantApplicationException
import com.ddd.support.exception.ApplicationException
import jakarta.validation.ConstraintViolationException
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.MethodArgumentNotValidException
import org.springframework.web.bind.annotation.ExceptionHandler
import org.springframework.web.bind.annotation.RestControllerAdvice

@RestControllerAdvice
class GlobalExceptionHandler {
    @ExceptionHandler(RestaurantApplicationException.RestaurantNotFoundException::class)
    fun handleRestaurantNotFoundException(
            exception: RestaurantApplicationException.RestaurantNotFoundException,
    ): ResponseEntity<ErrorResponse> {
        val errorResponse = ErrorResponse(message = exception.message)
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(errorResponse)
    }

    @ExceptionHandler(RestaurantApplicationException.RestaurantCreationFailedException::class)
    fun handleRestaurantCreationFailedException(
            exception: RestaurantApplicationException.RestaurantCreationFailedException,
    ): ResponseEntity<ErrorResponse> {
        val errorResponse = ErrorResponse(message = exception.message)
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(errorResponse)
    }

    @ExceptionHandler(RestaurantApplicationException.RestaurantMenuItemNotFoundException::class)
    fun handleRestaurantMenuItemNotFoundException(
            exception: RestaurantApplicationException.RestaurantMenuItemNotFoundException,
    ): ResponseEntity<ErrorResponse> {
        val errorResponse = ErrorResponse(message = exception.message)
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(errorResponse)
    }

    @ExceptionHandler(
            RestaurantApplicationException.RestaurantMenuItemCreationFailedException::class
    )
    fun handleRestaurantMenuItemCreationFailedException(
            exception: RestaurantApplicationException.RestaurantMenuItemCreationFailedException,
    ): ResponseEntity<ErrorResponse> {
        val errorResponse = ErrorResponse(message = exception.message)
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(errorResponse)
    }

    @ExceptionHandler(RestaurantApplicationException.RestaurantMenuItemUpdateFailedException::class)
    fun handleRestaurantMenuItemUpdateFailedException(
            exception: RestaurantApplicationException.RestaurantMenuItemUpdateFailedException,
    ): ResponseEntity<ErrorResponse> {
        val errorResponse = ErrorResponse(message = exception.message)
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(errorResponse)
    }

    @ExceptionHandler(RestaurantApplicationException.RestaurantMenuItemDeleteFailedException::class)
    fun handleRestaurantMenuItemDeleteFailedException(
            exception: RestaurantApplicationException.RestaurantMenuItemDeleteFailedException,
    ): ResponseEntity<ErrorResponse> {
        val errorResponse = ErrorResponse(message = exception.message)
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(errorResponse)
    }

    @ExceptionHandler(RestaurantApplicationException.GetRestaurantsFailedException::class)
    fun handleGetRestaurantsFailedException(
            exception: RestaurantApplicationException.GetRestaurantsFailedException,
    ): ResponseEntity<ErrorResponse> {
        val errorResponse = ErrorResponse(message = exception.message)
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(errorResponse)
    }

    @ExceptionHandler(RestaurantApplicationException.GetRestaurantFailedException::class)
    fun handleGetRestaurantFailedException(
            exception: RestaurantApplicationException.GetRestaurantFailedException,
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
                                        ?: "Invalid request",
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
                                        ?: "Invalid request",
                )
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(errorResponse)
    }
}
