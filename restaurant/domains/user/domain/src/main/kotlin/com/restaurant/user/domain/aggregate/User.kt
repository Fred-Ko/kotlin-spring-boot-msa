package com.restaurant.user.domain.aggregate

import com.restaurant.common.core.aggregate.AggregateRoot
import com.restaurant.common.core.domain.event.DomainEvent
import com.restaurant.user.domain.entity.Address
import com.restaurant.user.domain.event.UserEvent
import com.restaurant.user.domain.exception.UserDomainException
import com.restaurant.user.domain.vo.*
import java.time.Instant
import java.util.UUID
import com.restaurant.user.domain.aggregate.UserStatus
import com.restaurant.user.domain.aggregate.UserType

/**
 * User Aggregate Root
 */
data class User(
    val id: UserId, // Rule 11.5: val, public
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
    val updatedAt: Instant,
    // Rule 18: Manage events internally, ensure copy handles it
    private val eventsInternal: MutableList<DomainEvent> = mutableListOf()
) : AggregateRoot() {

    init {
        // Validation moved to factory methods or specific action methods
    }

    fun changePassword(newPassword: Password): User {
        // Check status etc.
        if (status == UserStatus.WITHDRAWN) throw UserDomainException.User.AlreadyWithdrawn()

        val updatedUser = this.copy(
            password = newPassword, // Assume newPassword is already encoded if needed
            version = this.version + 1,
            updatedAt = Instant.now(),
            eventsInternal = this.eventsInternal.toMutableList() // Rule 18: Copy events
        )
        updatedUser.addDomainEventInternal(UserEvent.PasswordChanged(userId = this.id, changedAt = updatedUser.updatedAt))
        return updatedUser
    }

    fun updateProfile(newName: Name, newPhoneNumber: PhoneNumber?): User {
         if (status == UserStatus.WITHDRAWN) throw UserDomainException.User.AlreadyWithdrawn()

        val updatedUser = this.copy(
            name = newName,
            phoneNumber = newPhoneNumber,
            version = this.version + 1,
            updatedAt = Instant.now(),
            eventsInternal = this.eventsInternal.toMutableList() // Rule 18: Copy events
        )
        updatedUser.addDomainEventInternal(UserEvent.ProfileUpdated(userId = this.id, name = newName.value, phoneNumber = newPhoneNumber?.value, updatedAt = updatedUser.updatedAt))
        return updatedUser
    }

     fun addAddress(addressData: Address): User {
         if (status == UserStatus.WITHDRAWN) throw UserDomainException.User.AlreadyWithdrawn()
         if (addresses.size >= MAX_ADDRESSES) throw UserDomainException.Address.LimitExceeded(MAX_ADDRESSES)
         if (addresses.any { it.id == addressData.id }) throw UserDomainException.Address.DuplicateAddressId(addressData.id.value.toString())

         val newAddresses = addresses + addressData
         // If new address is default, unset others. If no default exists, set new one as default.
         val newDefaultAddressId = if (addressData.isDefault) addressData.id else if (this.defaultAddressId == null) addressData.id else this.defaultAddressId

         val finalAddresses = newAddresses.map { addr ->
             if (addr.id == newDefaultAddressId) addr.copy(isDefault = true) else addr.copy(isDefault = false)
         }


         val updatedUser = this.copy(
             addresses = finalAddresses,
             defaultAddressId = newDefaultAddressId,
             version = this.version + 1,
             updatedAt = Instant.now(),
             eventsInternal = this.eventsInternal.toMutableList() // Rule 18: Copy events
         )

         updatedUser.addDomainEventInternal(
             UserEvent.AddressAdded(
                 userId = this.id,
                 address = addressData.toData(), // Assuming toData() exists in Address Entity
                 addedAt = updatedUser.updatedAt
             )
         )
         return updatedUser
     }

     fun updateAddress(addressId: AddressId, updatedAddressData: Address): User {
         if (status == UserStatus.WITHDRAWN) throw UserDomainException.User.AlreadyWithdrawn()
         if (addressId != updatedAddressData.id) throw UserDomainException.Address.IdMismatch(addressId.value.toString(), updatedAddressData.id.value.toString())

         val addressIndex = addresses.indexOfFirst { it.id == addressId }
         if (addressIndex == -1) throw UserDomainException.Address.NotFound(addressId.value.toString())

         val newAddresses = addresses.toMutableList()
         newAddresses[addressIndex] = updatedAddressData

         // Determine the new default ID
         val newDefaultAddressId = if (updatedAddressData.isDefault) {
             updatedAddressData.id // New one is default
         } else if (defaultAddressId == addressId) {
             // Default is being updated and is no longer default, pick another if possible
             newAddresses.filterNot { it.id == addressId }.firstOrNull()?.id
         } else {
             defaultAddressId // Keep the old default
         }

         // Ensure only one default address
          val finalAddresses = newAddresses.map { addr ->
              if (addr.id == newDefaultAddressId) addr.copy(isDefault = true) else addr.copy(isDefault = false)
          }

         val updatedUser = this.copy(
             addresses = finalAddresses.toList(),
             defaultAddressId = newDefaultAddressId,
             version = this.version + 1,
             updatedAt = Instant.now(),
             eventsInternal = this.eventsInternal.toMutableList() // Rule 18: Copy events
         )

         updatedUser.addDomainEventInternal(
             UserEvent.AddressUpdated(
                 userId = this.id,
                 address = updatedAddressData.toData(), // Assuming toData() exists
                 updatedAt = updatedUser.updatedAt
             )
         )
         return updatedUser
     }


     fun deleteAddress(addressId: AddressId): User {
         if (status == UserStatus.WITHDRAWN) throw UserDomainException.User.AlreadyWithdrawn()
         val addressToRemove = addresses.find { it.id == addressId }
             ?: throw UserDomainException.Address.NotFound(addressId.value.toString())

         if (addresses.size == 1) throw UserDomainException.Address.CannotDeleteLast()
         if (addressToRemove.isDefault) throw UserDomainException.Address.CannotDeleteDefault()

         val remainingAddresses = addresses.filterNot { it.id == addressId }

         val updatedUser = this.copy(
             addresses = remainingAddresses,
             // defaultAddressId remains the same as we cannot delete the default
             version = this.version + 1,
             updatedAt = Instant.now(),
             eventsInternal = this.eventsInternal.toMutableList() // Rule 18: Copy events
         )
         updatedUser.addDomainEventInternal(
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
            status = UserStatus.WITHDRAWN,
            version = this.version + 1,
            updatedAt = Instant.now(),
            eventsInternal = this.eventsInternal.toMutableList() // Rule 18: Copy events
        )
        updatedUser.addDomainEventInternal(UserEvent.Withdrawn(userId = this.id, withdrawnAt = updatedUser.updatedAt))
        return updatedUser
    }

    fun isActive(): Boolean {
        return this.status == UserStatus.ACTIVE
    }

    // Rule 18: Internal helper to add events
    internal fun addDomainEventInternal(event: DomainEvent) {
        this.eventsInternal.add(event)
    }

    // Rule 18: Implementation for AggregateRoot interface
    override fun getDomainEvents(): List<DomainEvent> = eventsInternal.toList()
    override fun clearDomainEvents() = eventsInternal.clear()

    companion object {
        private const val MAX_ADDRESSES = 5

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
             val defaultAddressId = initialAddresses.find { it.isDefault }?.id
                 ?: initialAddresses.firstOrNull()?.id // If no default, make the first one default

             val finalAddresses = initialAddresses.map { addr ->
                 if (addr.id == defaultAddressId) addr.copy(isDefault = true) else addr.copy(isDefault = false)
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
            user.addDomainEventInternal(UserEvent.Created(
                userId = id,
                username = username.value,
                email = email.value,
                name = name.value,
                phoneNumber = phoneNumber?.value,
                userType = userType.name, // Send enum name
                registeredAt = user.createdAt
                // address data can be added if needed
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
                updatedAt = updatedAt,
                eventsInternal = mutableListOf() // Rule 16: Events are cleared on reconstitution
            )
        }
    }
}
