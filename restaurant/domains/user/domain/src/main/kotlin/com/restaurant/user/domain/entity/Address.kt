package com.restaurant.user.domain.entity

import com.restaurant.user.domain.event.UserEvent
import com.restaurant.user.domain.exception.UserDomainException
import com.restaurant.user.domain.vo.AddressId
import java.time.Instant

/**
 * Address Domain Entity (Rule 11)
 * User Aggregate에 속하지만 자체 식별자를 가짐.
 */
data class Address internal constructor(
    val addressId: AddressId,
    val name: String,
    val streetAddress: String,
    val detailAddress: String?,
    val city: String,
    val state: String,
    val country: String,
    val zipCode: String,
    val isDefault: Boolean,
    val createdAt: Instant,
    val updatedAt: Instant,
    val version: Long = 0L,
) {
    init {
        if (streetAddress.isBlank()) {
            throw UserDomainException.Validation.InvalidAddressFormat("Street address cannot be blank.")
        }
        validateZipCode(zipCode)
        if (name.isBlank()) {
            throw UserDomainException.Validation.InvalidAddressFormat("Name cannot be blank.")
        }
        if (city.isBlank()) {
            throw UserDomainException.Validation.InvalidAddressFormat("City cannot be blank.")
        }
        if (state.isBlank()) {
            throw UserDomainException.Validation.InvalidAddressFormat("State cannot be blank.")
        }
        if (country.isBlank()) {
            throw UserDomainException.Validation.InvalidAddressFormat("Country cannot be blank.")
        }
    }

    fun updateDetails(
        name: String,
        streetAddress: String,
        detailAddress: String?,
        city: String,
        state: String,
        country: String,
        zipCode: String,
        isDefault: Boolean,
    ): Address {
        validateZipCode(zipCode)
        if (this.name == name &&
            this.streetAddress == streetAddress &&
            this.detailAddress == detailAddress &&
            this.city == city &&
            this.state == state &&
            this.country == country &&
            this.zipCode == zipCode &&
            this.isDefault == isDefault
        ) {
            return this
        }
        return this.copy(
            name = name,
            streetAddress = streetAddress,
            detailAddress = detailAddress,
            city = city,
            state = state,
            country = country,
            zipCode = zipCode,
            isDefault = isDefault,
            updatedAt = Instant.now(),
            version = this.version + 1,
        )
    }

    fun toData(): UserEvent.AddressData =
        UserEvent.AddressData(
            id = addressId.value.toString(),
            name = name,
            streetAddress = streetAddress,
            detailAddress = detailAddress,
            city = city,
            state = state,
            country = country,
            zipCode = zipCode,
            isDefault = isDefault,
        )

    companion object {
        private fun validateZipCode(zipCode: String) {
            if (zipCode.isBlank() || zipCode.length != 5 || !zipCode.all { it.isDigit() }) {
                throw UserDomainException.Validation.InvalidAddressFormat("Zip code must be 5 digits.")
            }
        }

        fun create(
            addressId: AddressId,
            name: String,
            streetAddress: String,
            detailAddress: String?,
            city: String,
            state: String,
            country: String,
            zipCode: String,
            isDefault: Boolean,
        ): Address {
            validateZipCode(zipCode)
            val now = Instant.now()
            return Address(
                addressId = addressId,
                name = name,
                streetAddress = streetAddress,
                detailAddress = detailAddress,
                city = city,
                state = state,
                country = country,
                zipCode = zipCode,
                isDefault = isDefault,
                createdAt = now,
                updatedAt = now,
                version = 0L,
            )
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
