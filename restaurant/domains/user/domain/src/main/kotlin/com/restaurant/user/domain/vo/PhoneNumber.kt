package com.restaurant.user.domain.vo
import com.restaurant.user.domain.exception.UserDomainException
import java.io.Serializable

@JvmInline
value class PhoneNumber private constructor(
    val value: String,
) : Serializable {
    companion object {
        private val PHONE_NUMBER_REGEX = Regex("^010-?\\d{4}-?\\d{4}$")

        fun of(value: String): PhoneNumber {
            val normalizedValue = value.replace("-", "")
            if (!PHONE_NUMBER_REGEX.matches(value)) {
                throw UserDomainException.Validation.InvalidPhoneNumberFormat("휴대폰 번호 형식이 올바르지 않습니다: $value")
            }

            return PhoneNumber(normalizedValue)
        }
    }

    override fun toString(): String = value
}
