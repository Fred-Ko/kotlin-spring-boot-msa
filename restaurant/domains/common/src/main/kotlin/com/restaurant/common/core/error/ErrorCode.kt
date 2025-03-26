package com.restaurant.common.core.error

import org.springframework.http.HttpStatus

interface ErrorCode {
  val code: String
  val message: String
  val status: HttpStatus
}

abstract class BaseErrorCode(
  override val code: String,
  override val message: String,
  override val status: HttpStatus,
) : ErrorCode {
  companion object {
    fun fromCode(
      errorCodes: List<ErrorCode>,
      code: String?,
    ): ErrorCode? = errorCodes.find { it.code == code }
  }
}
