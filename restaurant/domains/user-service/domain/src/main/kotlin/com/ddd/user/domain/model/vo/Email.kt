package com.ddd.user.domain.model.vo

@ConsistentCopyVisibility
data class Email private constructor(val value: String) {
    companion object {
        private val EMAIL_REGEX = "^[A-Za-z0-9+_.-]+@(.+)$".toRegex()

        fun of(value: String): Email {
            require(value.matches(EMAIL_REGEX)) { "올바른 이메일 형식이 아닙니다." }
            return Email(value)
        }
    }
}
