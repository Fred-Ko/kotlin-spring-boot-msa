package com.restaurant.domain.user.vo

data class Name private constructor(
    val value: String,
) {
    companion object {
        fun of(name: String): Name {
            require(name.isNotBlank()) { "이름은 공백일 수 없습니다." }
            return Name(name)
        }
    }
}
