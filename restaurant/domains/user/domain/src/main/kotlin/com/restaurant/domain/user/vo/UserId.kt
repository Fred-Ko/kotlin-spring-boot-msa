package com.restaurant.domain.user.vo

import kotlin.ConsistentCopyVisibility

@ConsistentCopyVisibility
data class UserId private constructor(
    val value: Long,
) {
    init {
        require(value > 0) { "UserId는 0보다 커야 합니다." }
    }

    companion object {
        fun of(value: Long): UserId = UserId(value)
    }

    override fun toString(): String = value.toString()
}
