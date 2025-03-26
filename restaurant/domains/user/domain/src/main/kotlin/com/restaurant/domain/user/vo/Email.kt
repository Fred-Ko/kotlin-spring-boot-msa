package com.restaurant.domain.user.vo

data class Email(
  val value: String,
) {
  init {
    require(value.matches(EMAIL_REGEX)) { "유효한 이메일 형식이 아닙니다." }
  }

  override fun toString(): String = value

  companion object {
    private val EMAIL_REGEX = Regex("^[A-Za-z0-9+_.-]+@[A-Za-z0-9.-]+$")
  }
}
