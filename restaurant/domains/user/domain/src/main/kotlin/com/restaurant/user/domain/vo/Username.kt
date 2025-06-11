package com.restaurant.user.domain.vo

import com.restaurant.user.domain.exception.UserDomainException
import java.io.Serializable

@JvmInline
value class Username private constructor(
    val value: String,
) : Serializable {
    // init block can be empty or removed if no other logic is needed

    companion object {
        fun of(value: String): Username {
            if (value.isBlank() || value.length < 3 || value.length > 20) {
                throw UserDomainException.Validation.InvalidUsernameFormat("사용자 이름은 3자 이상 20자 이하여야 하며, 공백일 수 없습니다: '$value'")
            }
            return Username(value)
        }
    }

    override fun toString(): String = value
}
