package com.restaurant.domain.user.vo

import kotlin.ConsistentCopyVisibility

@ConsistentCopyVisibility
data class Name private constructor(
    val value: String,
) {
    init {
        require(value.isNotBlank()) { "이름은 공백일 수 없습니다." }
    }

    companion object {
        fun of(name: String): Name = Name(name)
    }

    override fun toString(): String = value
}
