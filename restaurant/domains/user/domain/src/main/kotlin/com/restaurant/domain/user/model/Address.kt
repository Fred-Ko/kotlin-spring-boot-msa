package com.restaurant.domain.user.model

import com.restaurant.domain.user.exception.UserDomainException
import com.restaurant.domain.user.vo.AddressId

data class Address
    private constructor(
        val addressId: AddressId,
        val street: String,
        val detail: String,
        val zipCode: String,
        val isDefault: Boolean,
    ) {
        init {
            if (street.isBlank()) {
                throw UserDomainException.Validation.InvalidAddressFormat("도로명 주소는 비어있을 수 없습니다.")
            }
            if (zipCode.isBlank()) {
                throw UserDomainException.Validation.InvalidAddressFormat("우편번호는 비어있을 수 없습니다.")
            }
        }

        companion object {
            fun create(
                street: String,
                detail: String,
                zipCode: String,
                isDefault: Boolean = false,
            ): Address =
                Address(
                    addressId = AddressId.generate(),
                    street = street,
                    detail = detail,
                    zipCode = zipCode,
                    isDefault = isDefault,
                )

            fun reconstitute(
                addressId: AddressId,
                street: String,
                detail: String,
                zipCode: String,
                isDefault: Boolean,
            ): Address =
                Address(
                    addressId = addressId,
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
                addressId = this.addressId,
                street = street,
                detail = detail,
                zipCode = zipCode,
                isDefault = isDefault,
            )
    }
