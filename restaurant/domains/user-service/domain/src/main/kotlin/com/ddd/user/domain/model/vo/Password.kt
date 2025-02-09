package com.ddd.user.domain.model.vo

@JvmInline
value class Password(val value: String) {
    init {
        require(value.length >= MIN_LENGTH) {
            "Password must be at least ${MIN_LENGTH} characters long"
        }
    }

    companion object {
        const val MIN_LENGTH = 12

        fun of(value: String): Password {
            return Password(value)
        }
    }
}
