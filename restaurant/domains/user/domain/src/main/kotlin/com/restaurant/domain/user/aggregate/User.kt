package com.restaurant.domain.user.aggregate

import com.restaurant.common.domain.aggregate.AggregateRoot
import com.restaurant.domain.user.error.UserDomainException
import com.restaurant.domain.user.event.UserEvent
import com.restaurant.domain.user.model.Address
import com.restaurant.domain.user.vo.AddressId
import com.restaurant.domain.user.vo.Email
import com.restaurant.domain.user.vo.Name
import com.restaurant.domain.user.vo.Password
import com.restaurant.domain.user.vo.UserId
import java.time.LocalDateTime
import java.util.UUID

data class User
    private constructor(
        val id: UserId,
        val email: Email,
        val password: Password,
        val name: Name,
        val addresses: List<Address> = emptyList(),
        val createdAt: LocalDateTime = LocalDateTime.now(),
        val updatedAt: LocalDateTime = LocalDateTime.now(),
    ) : AggregateRoot() {
        companion object {
            fun create(
                email: Email,
                password: Password,
                name: Name,
            ): User {
                val userId = UserId.generate()
                val user = User(id = userId, email = email, password = password, name = name)
                val event =
                    UserEvent.Created(
                        userId = userId,
                        email = email.value,
                        name = name.value,
                        eventId = UUID.randomUUID().toString(),
                        occurredAt = user.createdAt,
                    )
                user.addDomainEvent(event)
                return user
            }

            fun reconstitute(
                id: UserId,
                email: Email,
                password: Password,
                name: Name,
                addresses: List<Address> = emptyList(),
                createdAt: LocalDateTime,
                updatedAt: LocalDateTime,
            ): User =
                User(
                    id = id,
                    email = email,
                    password = password,
                    name = name,
                    addresses = addresses,
                    createdAt = createdAt,
                    updatedAt = updatedAt,
                )
        }

        fun updateProfile(name: Name): User {
            val updated = this.copy(name = name, updatedAt = LocalDateTime.now())
            val event =
                UserEvent.ProfileUpdated(
                    userId = this.id,
                    name = name.value,
                    eventId = UUID.randomUUID().toString(),
                    occurredAt = updated.updatedAt,
                )
            updated.addDomainEvent(event)
            return updated
        }

        fun changePassword(encodedPassword: Password): User {
            val updated = this.copy(password = encodedPassword, updatedAt = LocalDateTime.now())
            val event =
                UserEvent.PasswordChanged(
                    userId = this.id,
                    eventId = UUID.randomUUID().toString(),
                    occurredAt = updated.updatedAt,
                )
            updated.addDomainEvent(event)
            return updated
        }

        fun addAddress(address: Address): User {
            val newAddresses =
                if (address.isDefault) {
                    addresses.map { it.update(isDefault = false) } + address
                } else {
                    if (addresses.isEmpty()) {
                        listOf(address.update(isDefault = true))
                    } else {
                        addresses + address
                    }
                }
            val updated = this.copy(addresses = newAddresses, updatedAt = LocalDateTime.now())
            val event =
                UserEvent.AddressAdded(
                    userId = this.id,
                    addressId = address.addressId,
                    eventId = UUID.randomUUID().toString(),
                    occurredAt = updated.updatedAt,
                )
            updated.addDomainEvent(event)
            return updated
        }

        fun updateAddress(
            addressId: AddressId,
            updatedAddress: Address,
        ): User {
            if (addressId != updatedAddress.addressId) {
                throw UserDomainException.Validation.InvalidAddressFormat(
                    "수정하려는 주소의 ID(${addressId.value})와 전달된 주소 데이터의 ID(${updatedAddress.addressId.value})가 일치하지 않습니다.",
                )
            }

            val existingAddress =
                addresses.find { it.addressId == addressId }
                    ?: throw UserDomainException.Address.NotFound(
                        userId = this.id.value.toString(),
                        addressId = addressId.value.toString(),
                    )

            val newAddresses =
                if (updatedAddress.isDefault) {
                    addresses.map {
                        when {
                            it.addressId == addressId -> updatedAddress
                            else -> it.update(isDefault = false)
                        }
                    }
                } else {
                    val currentDefault = addresses.find { it.isDefault }
                    if (currentDefault?.addressId == addressId) {
                        val addressesWithoutOriginal = addresses.filter { it.addressId != addressId }
                        val updatedList = addressesWithoutOriginal + updatedAddress
                        if (updatedList.size > 1) {
                            val firstOther = updatedList.first { it.addressId != addressId }
                            updatedList.map { adr ->
                                if (adr.addressId == firstOther.addressId) adr.update(isDefault = true) else adr
                            }
                        } else {
                            listOf(updatedAddress.update(isDefault = true))
                        }
                    } else {
                        addresses.map { if (it.addressId == addressId) updatedAddress else it }
                    }
                }

            val ensuredAddresses =
                if (newAddresses.none { it.isDefault } && newAddresses.isNotEmpty()) {
                    newAddresses.mapIndexed { index, adr -> if (index == 0) adr.update(isDefault = true) else adr }
                } else {
                    newAddresses
                }

            val updated = this.copy(addresses = ensuredAddresses, updatedAt = LocalDateTime.now())
            val event =
                UserEvent.AddressUpdated(
                    userId = this.id,
                    addressId = addressId,
                    eventId = UUID.randomUUID().toString(),
                    occurredAt = updated.updatedAt,
                )
            updated.addDomainEvent(event)
            return updated
        }

        fun removeAddress(addressId: AddressId): User {
            val existingAddress =
                addresses.find { it.addressId == addressId }
                    ?: throw UserDomainException.Address.NotFound(
                        userId = this.id.value.toString(),
                        addressId = addressId.value.toString(),
                    )

            if (addresses.size == 1) {
                throw UserDomainException.Address.CannotRemoveLastAddress(
                    addressId = addressId.value.toString(),
                )
            }

            val newAddresses = addresses.filter { it.addressId != addressId }
            val ensuredAddresses =
                if (existingAddress.isDefault && newAddresses.isNotEmpty()) {
                    newAddresses.mapIndexed { index, adr -> if (index == 0) adr.update(isDefault = true) else adr }
                } else {
                    newAddresses
                }

            val updated = this.copy(addresses = ensuredAddresses, updatedAt = LocalDateTime.now())
            val event =
                UserEvent.AddressRemoved(
                    userId = this.id,
                    addressId = addressId,
                    eventId = UUID.randomUUID().toString(),
                    occurredAt = updated.updatedAt,
                )
            updated.addDomainEvent(event)
            return updated
        }

        private fun copy(
            email: Email = this.email,
            password: Password = this.password,
            name: Name = this.name,
            addresses: List<Address> = this.addresses,
            createdAt: LocalDateTime = this.createdAt,
            updatedAt: LocalDateTime = this.updatedAt,
        ): User {
            val copiedUser =
                User(
                    id = this.id,
                    email = email,
                    password = password,
                    name = name,
                    addresses = addresses,
                    createdAt = createdAt,
                    updatedAt = updatedAt,
                )
            return copiedUser
        }
    }
