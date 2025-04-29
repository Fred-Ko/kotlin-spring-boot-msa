package com.restaurant.user.domain.entity

import com.restaurant.user.domain.event.UserEvent
import com.restaurant.user.domain.exception.UserDomainException
import com.restaurant.user.domain.vo.AddressId
import java.time.Instant

/**
 * Address Domain Entity (Rule 11)
 * User Aggregate에 속하지만 자체 식별자를 가짐.
 */
data class Address private constructor(
    val addressId: AddressId,
    val street: String,
    val detail: String,
    val zipCode: String,
    var isDefault: Boolean,
    val createdAt: Instant,
    var updatedAt: Instant,
    var version: Long = 0L,
) {
    init {
        if (street.isBlank()) {
            throw UserDomainException.Validation.InvalidAddressFormat("Street cannot be blank.")
        }
        validateZipCode(zipCode)
    }

    fun updateDetails(
        street: String,
        detail: String,
        zipCode: String,
        isDefault: Boolean,
    ): Address {
        validateZipCode(zipCode)
        if (this.street == street && this.detail == detail && this.zipCode == zipCode && this.isDefault == isDefault) {
            return this
        }
        return this.copy(
            street = street,
            detail = detail,
            zipCode = zipCode,
            isDefault = isDefault,
            updatedAt = Instant.now(),
            version = this.version + 1,
        )
    }

    fun toData(): UserEvent.AddressData =
        UserEvent.AddressData(
            addressId = this.addressId.value.toString(),
            street = this.street,
            detail = this.detail,
            zipCode = this.zipCode,
            isDefault = this.isDefault,
        )

    companion object {
        private fun validateZipCode(zipCode: String) {
            if (zipCode.isBlank() || zipCode.length != 5 || !zipCode.all { it.isDigit() }) {
                throw UserDomainException.Validation.InvalidAddressFormat("Zip code must be 5 digits.")
            }
        }

        fun create(
            addressId: AddressId,
            street: String,
            detail: String,
            zipCode: String,
            isDefault: Boolean,
            version: Long = 0L,
        ): Address {
            validateZipCode(zipCode)
            val now = Instant.now()
            return Address(
                addressId = addressId,
                street = street,
                detail = detail,
                zipCode = zipCode,
                isDefault = isDefault,
                createdAt = now,
                updatedAt = now,
                version = version,
            )
        }

        fun reconstitute(
            addressId: AddressId,
            street: String,
            detail: String,
            zipCode: String,
            isDefault: Boolean,
            createdAt: Instant,
            updatedAt: Instant,
            version: Long,
        ): Address {
            // No validation needed on reconstitute, assuming data is valid
            return Address(addressId, street, detail, zipCode, isDefault, createdAt, updatedAt, version)
        }
    }

    fun markAsDefault(): Address {
        if (this.isDefault) return this
        return this.copy(
            isDefault = true,
            updatedAt = Instant.now(),
            version = this.version + 1,
        )
    }

    fun markAsNonDefault(): Address {
        if (!this.isDefault) return this
        return this.copy(
            isDefault = false,
            updatedAt = Instant.now(),
            version = this.version + 1,
        )
    }
}
