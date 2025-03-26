package com.restaurant.application.user.common

import com.restaurant.common.core.error.ErrorCode
import org.springframework.http.HttpStatus

enum class UserErrorCode(
  override val code: String,
  override val message: String,
  override val status: HttpStatus,
) : ErrorCode {
  NOT_FOUND("USER_NOT_FOUND", "사용자를 찾을 수 없습니다.", HttpStatus.NOT_FOUND),
  DUPLICATE_EMAIL("USER_DUPLICATE_EMAIL", "이미 등록된 이메일입니다.", HttpStatus.CONFLICT),
  INVALID_CREDENTIALS(
    "USER_INVALID_CREDENTIALS",
    "이메일 또는 비밀번호가 올바르지 않습니다.",
    HttpStatus.UNAUTHORIZED,
  ),
  INVALID_INPUT("USER_INVALID_INPUT", "입력값이 유효하지 않습니다.", HttpStatus.BAD_REQUEST),
  INVALID_PASSWORD("USER_INVALID_PASSWORD", "현재 비밀번호가 올바르지 않습니다.", HttpStatus.BAD_REQUEST),
  DELETION_FAILED("USER_DELETION_FAILED", "사용자 삭제에 실패했습니다.", HttpStatus.BAD_REQUEST),
  UPDATE_FAILED("USER_UPDATE_FAILED", "사용자 정보 수정에 실패했습니다.", HttpStatus.BAD_REQUEST),
  SYSTEM_ERROR("USER_SYSTEM_ERROR", "시스템 오류가 발생했습니다.", HttpStatus.INTERNAL_SERVER_ERROR),
  ;

  companion object {
    fun fromCode(code: String?): UserErrorCode = entries.find { it.code == code } ?: SYSTEM_ERROR
  }
}
