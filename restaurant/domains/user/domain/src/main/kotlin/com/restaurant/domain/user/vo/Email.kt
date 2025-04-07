package com.restaurant.domain.user.vo

import kotlin.ConsistentCopyVisibility

@ConsistentCopyVisibility
data class Email private constructor(
    val value: String,
) {
    init {
        require(value.matches(EMAIL_REGEX)) { "유효한 이메일 형식이 아닙니다." }
    }

    companion object {
        private val EMAIL_REGEX = Regex("^[A-Za-z0-9+_.-]+@[A-Za-z0-9.-]+\\.[A-Za-z0-9.-]+$")

        fun of(value: String): Email = Email(value)
    }

    override fun toString(): String = value
}
