package com.restaurant.user.domain.vo
import com.restaurant.user.domain.exception.UserDomainException
import java.util.regex.Pattern

import java.io.Serializable

@JvmInline
value class PhoneNumber private constructor(
    val value: String,
) : Serializable {
    init {
        if (!value.matches(PHONE_NUMBER_REGEX)) {
            throw UserDomainException.Validation.InvalidPhoneNumberFormat(value)
        }
    }

    companion object {
        // 대한민국 휴대폰 번호 형식 (010-xxxx-xxxx 또는 010xxxxxxxx)
        private val PHONE_NUMBER_REGEX = Regex("^010-?\\d{4}-?\\d{4}$")

        fun of(value: String): PhoneNumber {
            val normalizedValue = value.replace("-", "") // 하이픈 제거하여 검증
            if (!PHONE_NUMBER_REGEX.matches(value)) {
                throw UserDomainException.Validation.InvalidPhoneNumberFormat("휴대폰 번호 형식이 올바르지 않습니다: $value")
            }
            // 저장 시 하이픈 포함 또는 미포함 선택 가능 (여기서는 미포함 저장)
            return PhoneNumber(normalizedValue)
        }
    }

    override fun toString(): String = value
}
