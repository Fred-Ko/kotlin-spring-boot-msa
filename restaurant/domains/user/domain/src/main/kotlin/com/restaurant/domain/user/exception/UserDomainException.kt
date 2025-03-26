package com.restaurant.domain.user.exception

import com.restaurant.common.core.exception.DomainException

open class UserDomainException(
  message: String,
) : DomainException(message)

class UserNotFoundException(
  userId: String,
) : UserDomainException("사용자를 찾을 수 없습니다: $userId")

class InvalidCredentialsException : UserDomainException("이메일 또는 비밀번호가 올바르지 않습니다.")

class DuplicateEmailException(
  email: String,
) : UserDomainException("이미 등록된 이메일입니다: $email")
