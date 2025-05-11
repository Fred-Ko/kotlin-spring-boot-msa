package com.restaurant.user.domain.vo
import com.restaurant.user.domain.exception.UserDomainException
import java.io.Serializable

@JvmInline
value class Name private constructor(
    val value: String,
) : Serializable {
    init {
        if (value.isBlank()) {
            throw UserDomainException.Validation.InvalidNameFormat("Invalid name format: $value")
        }
    }

    companion object {
        private const val MIN_LENGTH = 2
        private const val MAX_LENGTH = 50

        fun of(value: String): Name {
            if (value.isBlank() || value.length < MIN_LENGTH || value.length > MAX_LENGTH) {
                throw UserDomainException.Validation.InvalidNameFormat("이름은 $MIN_LENGTH 자 이상 $MAX_LENGTH 자 이하이어야 하며, 공백일 수 없습니다: '$value'")
            }
            return Name(value)
        }
    }

    override fun toString(): String = value
}
