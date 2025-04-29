package com.restaurant.user.domain.aggregate

import com.restaurant.common.domain.aggregate.AggregateRoot
import com.restaurant.common.domain.event.DomainEvent
import com.restaurant.user.domain.entity.Address
import com.restaurant.user.domain.event.UserEvent
import com.restaurant.user.domain.vo.Username
import com.restaurant.user.domain.vo.UserId
import com.restaurant.user.domain.vo.Password
import com.restaurant.user.domain.vo.Email
import com.restaurant.user.domain.vo.Name
import com.restaurant.user.domain.vo.PhoneNumber
import com.restaurant.user.domain.vo.AddressId
import com.restaurant.user.domain.exception.UserDomainException
import java.time.Instant

/**
 * User Aggregate Root
 */
data class User(
    val id: UserId,
    val username: Username,
    val password: Password, // Assume this is the encoded password
    val email: Email,
    val name: Name,
    val phoneNumber: PhoneNumber?,
    val userType: UserType, // Use imported type
    val status: UserStatus, // Use imported type
    val addresses: List<Address> = listOf(),
    val defaultAddressId: AddressId? = null,
    val version: Long,
    val createdAt: Instant,
    val updatedAt: Instant
) : AggregateRoot() {
    @Transient
    private val _domainEvents: MutableList<DomainEvent> = mutableListOf()

    override fun getDomainEvents(): List<DomainEvent> = _domainEvents.toList()
    override fun clearDomainEvents() = _domainEvents.clear()
    internal fun addDomainEvent(event: DomainEvent) {
        this._domainEvents.add(event)
    }

    fun changePassword(newPassword: Password): User {
        // Check status etc.
        if (status == UserStatus.WITHDRAWN) throw UserDomainException.User.AlreadyWithdrawn()

        val updatedUser = this.copy(
            id = this.id,
            username = this.username,
            password = newPassword, 
            email = this.email,
            name = this.name,
            phoneNumber = this.phoneNumber,
            userType = this.userType,
            status = this.status,
            addresses = this.addresses,
            defaultAddressId = this.defaultAddressId,
            version = this.version + 1,
            createdAt = this.createdAt,
            updatedAt = Instant.now()
        )
        updatedUser.addDomainEvent(UserEvent.PasswordChanged(userId = this.id, changedAt = updatedUser.updatedAt))
        return updatedUser
    }

    fun updateProfile(newName: Name, newPhoneNumber: PhoneNumber?): User {
         if (status == UserStatus.WITHDRAWN) throw UserDomainException.User.AlreadyWithdrawn()

        val updatedUser = this.copy(
            id = this.id,
            username = this.username,
            password = this.password, 
            email = this.email,
            name = newName,
            phoneNumber = newPhoneNumber,
            userType = this.userType,
            status = this.status,
            addresses = this.addresses,
            defaultAddressId = this.defaultAddressId,
            version = this.version + 1,
            createdAt = this.createdAt,
            updatedAt = Instant.now()
        )
        updatedUser.addDomainEvent(UserEvent.ProfileUpdated(userId = this.id, name = newName.value, phoneNumber = newPhoneNumber?.value, updatedAt = updatedUser.updatedAt))
        return updatedUser
    }

    fun addAddress(addressData: Address): User {
        if (status == UserStatus.WITHDRAWN) throw UserDomainException.User.AlreadyWithdrawn()
        if (addresses.size >= User.MAX_ADDRESSES) throw UserDomainException.Address.LimitExceeded(User.MAX_ADDRESSES)
        if (addresses.any { it.addressId == addressData.addressId }) throw UserDomainException.Address.DuplicateAddressId(addressData.addressId.value.toString())

        val newAddresses = addresses + addressData
        val newDefaultAddressId = if (addressData.isDefault) addressData.addressId else if (this.defaultAddressId == null) addressData.addressId else this.defaultAddressId

        val finalAddresses = newAddresses.map { addr ->
            if (addr.addressId == newDefaultAddressId) addr.copy(isDefault = true) else addr.copy(isDefault = false)
        }

        val updatedUser = this.copy(
            id = this.id,
            username = this.username,
            password = this.password, 
            email = this.email,
            name = this.name,
            phoneNumber = this.phoneNumber,
            userType = this.userType,
            status = this.status,
            addresses = finalAddresses,
            defaultAddressId = newDefaultAddressId,
            version = this.version + 1,
            createdAt = this.createdAt,
            updatedAt = Instant.now()
        )

        updatedUser.addDomainEvent(
            UserEvent.AddressAdded(
                userId = this.id,
                address = addressData.toData(), 
                addedAt = updatedUser.updatedAt
            )
        )
        return updatedUser
    }

    fun updateAddress(addressId: AddressId, updatedAddressData: Address): User {
        if (status == UserStatus.WITHDRAWN) throw UserDomainException.User.AlreadyWithdrawn()
        if (addressId != updatedAddressData.addressId) throw UserDomainException.Address.IdMismatch(addressId.value.toString(), updatedAddressData.addressId.value.toString())

        val addressIndex = addresses.indexOfFirst { it.addressId == addressId }
        if (addressIndex == -1) throw UserDomainException.Address.NotFound(addressId.value.toString())

        val newAddresses = addresses.toMutableList()
        newAddresses[addressIndex] = updatedAddressData

        // Determine the new default ID
        val newDefaultAddressId = if (updatedAddressData.isDefault) {
            updatedAddressData.addressId // New one is default
        } else if (defaultAddressId == addressId) {
            // Default is being updated and is no longer default, pick another if possible
            newAddresses.filterNot { it.addressId == addressId }.firstOrNull()?.addressId
        } else {
            defaultAddressId // Keep the old default
        }

        // Ensure only one default address
        val finalAddresses = newAddresses.map { addr ->
            if (addr.addressId == newDefaultAddressId) addr.copy(isDefault = true) else addr.copy(isDefault = false)
        }

        val updatedUser = this.copy(
            id = this.id,
            username = this.username,
            password = this.password, 
            email = this.email,
            name = this.name,
            phoneNumber = this.phoneNumber,
            userType = this.userType,
            status = this.status,
            addresses = finalAddresses,
            defaultAddressId = newDefaultAddressId,
            version = this.version + 1,
            createdAt = this.createdAt,
            updatedAt = Instant.now()
        )

        updatedUser.addDomainEvent(
            UserEvent.AddressUpdated(
                userId = this.id,
                address = updatedAddressData.toData(), 
                updatedAt = updatedUser.updatedAt
            )
        )
        return updatedUser
    }

    fun deleteAddress(addressId: AddressId): User {
        if (status == UserStatus.WITHDRAWN) throw UserDomainException.User.AlreadyWithdrawn()
        val addressToRemove = addresses.find { it.addressId == addressId }
            ?: throw UserDomainException.Address.NotFound(addressId.value.toString())

        if (addresses.size == 1) throw UserDomainException.Address.CannotDeleteLast()
        if (addressToRemove.isDefault) throw UserDomainException.Address.CannotDeleteDefault()

        val remainingAddresses = addresses.filterNot { it.addressId == addressId }

        val updatedUser = this.copy(
            id = this.id,
            username = this.username,
            password = this.password, 
            email = this.email,
            name = this.name,
            phoneNumber = this.phoneNumber,
            userType = this.userType,
            status = this.status,
            addresses = remainingAddresses,
            defaultAddressId = this.defaultAddressId,
            version = this.version + 1,
            createdAt = this.createdAt,
            updatedAt = Instant.now()
        )
        updatedUser.addDomainEvent(
            UserEvent.AddressDeleted(
                userId = this.id,
                addressId = addressId.value.toString(),
                deletedAt = updatedUser.updatedAt
            )
        )
        return updatedUser
    }

    fun withdraw(): User {
        if (status == UserStatus.WITHDRAWN) throw UserDomainException.User.AlreadyWithdrawn()
        if (userType == UserType.ADMIN) throw UserDomainException.User.AdminCannotBeWithdrawn()

        val updatedUser = this.copy(
            id = this.id,
            username = this.username,
            password = this.password, 
            email = this.email,
            name = this.name,
            phoneNumber = this.phoneNumber,
            userType = this.userType,
            status = UserStatus.WITHDRAWN,
            addresses = this.addresses,
            defaultAddressId = this.defaultAddressId,
            version = this.version + 1,
            createdAt = this.createdAt,
            updatedAt = Instant.now()
        )
        updatedUser.addDomainEvent(UserEvent.Withdrawn(userId = this.id, withdrawnAt = updatedUser.updatedAt))
        return updatedUser
    }

    fun isActive(): Boolean {
        return this.status == UserStatus.ACTIVE
    }

    companion object {
        const val MAX_ADDRESSES = 5

        // Rule 16: Create factory method
        fun create(
            id: UserId,
            username: Username,
            password: Password, // Assume this is the encoded password
            email: Email,
            name: Name,
            phoneNumber: PhoneNumber?,
            userType: UserType = UserType.CUSTOMER,
            initialAddresses: List<Address> = emptyList()
        ): User {
             val now = Instant.now()
             // Validate initial addresses
             if (initialAddresses.count { it.isDefault } > 1) {
                 throw UserDomainException.Address.MultipleDefaultsOnInit()
             }
             val defaultAddressId = initialAddresses.find { it.isDefault }?.addressId
                 ?: initialAddresses.firstOrNull()?.addressId // If no default, make the first one default

             val finalAddresses = initialAddresses.map { addr ->
                 if (addr.addressId == defaultAddressId) addr.copy(isDefault = true) else addr.copy(isDefault = false)
             }

            val user = User(
                id = id,
                username = username,
                password = password,
                email = email,
                name = name,
                phoneNumber = phoneNumber,
                userType = userType,
                status = UserStatus.ACTIVE, // Initial status
                addresses = finalAddresses,
                defaultAddressId = defaultAddressId,
                createdAt = now,
                updatedAt = now,
                version = 0L // Initial version
            )
            // Rule 16, 18: Add creation event
            user.addDomainEvent(UserEvent.Created(
                userId = id,
                username = username.value,
                email = email.value,
                name = name.value,
                phoneNumber = phoneNumber?.value,
                userType = userType.name, // Send enum name
                registeredAt = user.createdAt
            ))
            return user
        }

        // Rule 16: Reconstitute factory method
        fun reconstitute(
            id: UserId,
            username: Username,
            password: Password,
            email: Email,
            name: Name,
            phoneNumber: PhoneNumber?,
            userType: UserType,
            status: UserStatus,
            addresses: List<Address>,
            defaultAddressId: AddressId?,
            version: Long,
            createdAt: Instant,
            updatedAt: Instant
        ): User {
            return User(
                id = id,
                username = username,
                password = password,
                email = email,
                name = name,
                phoneNumber = phoneNumber,
                userType = userType,
                status = status,
                addresses = addresses,
                defaultAddressId = defaultAddressId,
                version = version,
                createdAt = createdAt,
                updatedAt = updatedAt
            )
        }
    }
}
