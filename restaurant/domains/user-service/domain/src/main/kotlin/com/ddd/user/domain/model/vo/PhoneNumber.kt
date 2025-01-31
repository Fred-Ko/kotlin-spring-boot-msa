package com.ddd.user.domain.model.vo

@ConsistentCopyVisibility
data class PhoneNumber private constructor(val value: String) {
    companion object {
        private val PHONE_REGEX = "^\\d{3}-\\d{3,4}-\\d{4}$".toRegex()

        fun of(value: String): PhoneNumber {
            require(value.matches(PHONE_REGEX)) { "올바른 전화번호 형식이 아닙니다. (예: 010-1234-5678)" }
            return PhoneNumber(value)
        }
    }
}
