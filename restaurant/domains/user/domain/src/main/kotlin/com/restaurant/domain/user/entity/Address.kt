package com.restaurant.domain.user.entity

import kotlin.ConsistentCopyVisibility

@ConsistentCopyVisibility
data class Address
    private constructor(
        val id: Long? = null,
        val street: String,
        val detail: String,
        val zipCode: String,
        val isDefault: Boolean,
    ) {
        companion object {
            fun create(
                street: String,
                detail: String,
                zipCode: String,
                isDefault: Boolean = false,
            ): Address {
                require(street.isNotBlank()) { "도로명 주소는 비어있을 수 없습니다." }
                require(zipCode.isNotBlank()) { "우편번호는 비어있을 수 없습니다." }

                return Address(
                    street = street,
                    detail = detail,
                    zipCode = zipCode,
                    isDefault = isDefault,
                )
            }

            fun reconstitute(
                id: Long,
                street: String,
                detail: String,
                zipCode: String,
                isDefault: Boolean,
            ): Address =
                Address(
                    id = id,
                    street = street,
                    detail = detail,
                    zipCode = zipCode,
                    isDefault = isDefault,
                )
        }

        fun update(
            street: String = this.street,
            detail: String = this.detail,
            zipCode: String = this.zipCode,
            isDefault: Boolean = this.isDefault,
        ): Address =
            Address(
                id = this.id,
                street = street,
                detail = detail,
                zipCode = zipCode,
                isDefault = isDefault,
            )
    }
