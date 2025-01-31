package com.ddd.user.domain.model.vo

@ConsistentCopyVisibility
data class UserName private constructor(val value: String) {
    companion object {
        fun of(value: String): UserName {
            require(value.isNotBlank()) { "이름은 비어있을 수 없습니다." }
            require(value.length in 2..50) { "이름은 2-50자 사이여야 합니다." }
            return UserName(value)
        }
    }
}
