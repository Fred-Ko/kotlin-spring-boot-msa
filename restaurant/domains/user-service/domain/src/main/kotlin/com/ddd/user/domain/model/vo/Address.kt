package com.ddd.user.domain.model.vo

@ConsistentCopyVisibility
data class Address
private constructor(val street: String, val city: String, val state: String, val zipCode: String) {
    companion object {
        fun of(street: String, city: String, state: String, zipCode: String): Address {
            require(street.isNotBlank()) { "도로명 주소는 비어있을 수 없습니다." }
            require(city.isNotBlank()) { "도시는 비어있을 수 없습니다." }
            require(state.isNotBlank()) { "시/도는 비어있을 수 없습니다." }
            require(zipCode.matches("^\\d{5}$".toRegex())) { "우편번호는 5자리 숫자여야 합니다." }

            return Address(street, city, state, zipCode)
        }
    }

    override fun toString(): String {
        return "$street, $city, $state $zipCode"
    }
}
