package com.restaurant.user.domain.aggregate

import com.restaurant.common.domain.aggregate.AggregateRoot
import com.restaurant.common.domain.event.DomainEvent
import com.restaurant.user.domain.entity.Address
import com.restaurant.user.domain.event.UserEvent
import com.restaurant.user.domain.exception.UserDomainException
import com.restaurant.user.domain.vo.AddressId
import com.restaurant.user.domain.vo.Email
import com.restaurant.user.domain.vo.Name
import com.restaurant.user.domain.vo.Password
import com.restaurant.user.domain.vo.PhoneNumber
import com.restaurant.user.domain.vo.UserId
import com.restaurant.user.domain.vo.UserStatus
import com.restaurant.user.domain.vo.UserType
import com.restaurant.user.domain.vo.Username
import java.time.Instant

data class User private constructor(
    val id: UserId,
    val username: Username,
    val password: Password,
    val email: Email,
    val name: Name,
    val phoneNumber: PhoneNumber?,
    val userType: UserType,
    val status: UserStatus,
    val addresses: List<Address> = listOf(),
    val defaultAddressId: AddressId? = null,
    val version: Long,
    val createdAt: Instant,
    val updatedAt: Instant,
    private val eventsInternal: MutableList<DomainEvent> = mutableListOf(),
) : AggregateRoot() {
    internal fun addDomainEventInternal(event: DomainEvent) {
        this.eventsInternal.add(event)
    }

    override fun getDomainEvents(): List<DomainEvent> = eventsInternal.toList()

    override fun clearDomainEvents() = eventsInternal.clear()

    fun changePassword(newPassword: Password): User {
        if (status == UserStatus.WITHDRAWN) throw UserDomainException.User.AlreadyWithdrawn()

        val updatedUser =
            copy(
                password = newPassword,
                version = this.version + 1,
                updatedAt = Instant.now(),
                eventsInternal = this.eventsInternal.toMutableList(),
            )
        updatedUser.addDomainEventInternal(
            UserEvent.PasswordChanged(
                userId = this.id,
                occurredAt = updatedUser.updatedAt,
            ),
        )
        return updatedUser
    }

    fun updateProfile(
        newName: Name,
        newPhoneNumber: PhoneNumber?,
    ): User {
        if (status == UserStatus.WITHDRAWN) throw UserDomainException.User.AlreadyWithdrawn()

        val updatedUser =
            copy(
                name = newName,
                phoneNumber = newPhoneNumber,
                version = this.version + 1,
                updatedAt = Instant.now(),
                eventsInternal = this.eventsInternal.toMutableList(),
            )
        updatedUser.addDomainEventInternal(
            UserEvent.ProfileUpdated(
                name = newName.value,
                phoneNumber = newPhoneNumber?.value,
                userId = this.id,
                occurredAt = updatedUser.updatedAt,
            ),
        )
        return updatedUser
    }

    fun addAddress(addressData: Address): User {
        if (status == UserStatus.WITHDRAWN) throw UserDomainException.User.AlreadyWithdrawn()
        if (addresses.size >= MAX_ADDRESSES) throw UserDomainException.Address.LimitExceeded(MAX_ADDRESSES)
        if (addresses.any { it.addressId == addressData.addressId }) {
            throw UserDomainException.Address.DuplicateAddressId(addressData.addressId.value.toString())
        }
        val newAddresses = addresses.toMutableList()
        if (addressData.isDefault) {
            newAddresses.forEachIndexed { idx, addr -> newAddresses[idx] = addr.copy(isDefault = false) }
        }
        newAddresses.add(addressData)
        val currentDefault = newAddresses.find { it.isDefault }
        val finalDefaultAddressId = currentDefault?.addressId ?: newAddresses.firstOrNull()?.addressId
        val finalAddresses = newAddresses.map { addr -> addr.copy(isDefault = addr.addressId == finalDefaultAddressId) }
        val updatedUser = copy(
            addresses = finalAddresses,
            defaultAddressId = finalDefaultAddressId,
            version = this.version + 1,
            updatedAt = Instant.now(),
            eventsInternal = this.eventsInternal.toMutableList(),
        )

        updatedUser.addDomainEventInternal(
            UserEvent.AddressRegistered(
                address = addressData.toData(),
                userId = this.id,
                occurredAt = updatedUser.updatedAt,
            ),
        )
        return updatedUser
    }

    fun updateAddress(updatedAddressData: Address): User {
        if (status == UserStatus.WITHDRAWN) throw UserDomainException.User.AlreadyWithdrawn()
        val addressIndex = addresses.indexOfFirst { it.addressId == updatedAddressData.addressId }
        if (addressIndex == -1) throw UserDomainException.Address.NotFound(updatedAddressData.addressId.value.toString())
        val newAddresses = addresses.toMutableList()
        if (updatedAddressData.isDefault) {
            newAddresses.forEachIndexed { idx, addr -> if (addr.addressId != updatedAddressData.addressId) newAddresses[idx] = addr.copy(isDefault = false) }
        }
        newAddresses[addressIndex] = updatedAddressData
        val currentDefault = newAddresses.find { it.isDefault }
        val finalDefaultAddressId = currentDefault?.addressId ?: if (newAddresses.isNotEmpty()) newAddresses.first().addressId else null
        val finalAddresses = newAddresses.map { addr -> addr.copy(isDefault = addr.addressId == finalDefaultAddressId) }
        val updatedUser = copy(
            addresses = finalAddresses,
            defaultAddressId = finalDefaultAddressId,
            version = this.version + 1,
            updatedAt = Instant.now(),
            eventsInternal = this.eventsInternal.toMutableList(),
        )

        updatedUser.addDomainEventInternal(
            UserEvent.AddressUpdated(
                address = updatedAddressData.toData(),
                userId = this.id,
                occurredAt = updatedUser.updatedAt,
            ),
        )
        return updatedUser
    }

    fun deleteAddress(addressId: AddressId): User {
        if (status == UserStatus.WITHDRAWN) throw UserDomainException.User.AlreadyWithdrawn()
        val addressToRemove =
            addresses.find { it.addressId == addressId }
                ?: throw UserDomainException.Address.NotFound(addressId.value.toString())

        if (addresses.size == 1) throw UserDomainException.Address.CannotDeleteLast()
        if (addressToRemove.isDefault) throw UserDomainException.Address.CannotDeleteDefault()

        val remainingAddresses = addresses.filterNot { it.addressId == addressId }

        val updatedUser =
            copy(
                addresses = remainingAddresses,
                version = this.version + 1,
                updatedAt = Instant.now(),
                eventsInternal = this.eventsInternal.toMutableList(),
            )
        updatedUser.addDomainEventInternal(
            UserEvent.AddressDeleted(
                addressId = addressId.value.toString(),
                userId = this.id,
                occurredAt = updatedUser.updatedAt,
            ),
        )
        return updatedUser
    }

    fun withdraw(): User {
        if (status == UserStatus.WITHDRAWN) throw UserDomainException.User.AlreadyWithdrawn()
        if (userType == UserType.ADMIN) throw UserDomainException.User.AdminCannotBeWithdrawn()

        val updatedUser =
            copy(
                status = UserStatus.WITHDRAWN,
                version = this.version + 1,
                updatedAt = Instant.now(),
                eventsInternal = this.eventsInternal.toMutableList(),
            )
        updatedUser.addDomainEventInternal(
            UserEvent.Withdrawn(
                userId = this.id,
                occurredAt = updatedUser.updatedAt,
            ),
        )
        return updatedUser
    }

    fun isActive(): Boolean = this.status == UserStatus.ACTIVE

    companion object {
        const val MAX_ADDRESSES = 5

        fun create(
            id: UserId,
            username: Username,
            password: Password,
            email: Email,
            name: Name,
            phoneNumber: PhoneNumber?,
            userType: UserType = UserType.CUSTOMER,
            initialAddresses: List<Address> = emptyList(),
        ): User {
            val now = Instant.now()
            if (initialAddresses.count { it.isDefault } > 1) {
                throw UserDomainException.Address.MultipleDefaultsOnInit()
            }
            val defaultAddressId =
                initialAddresses.find { it.isDefault }?.addressId
                    ?: initialAddresses.firstOrNull()?.addressId

            val finalAddresses =
                initialAddresses.map { addr ->
                    if (addr.addressId == defaultAddressId) addr.copy(isDefault = true) else addr.copy(isDefault = false)
                }

            val user =
                User(
                    id = id,
                    username = username,
                    password = password,
                    email = email,
                    name = name,
                    phoneNumber = phoneNumber,
                    userType = userType,
                    status = UserStatus.ACTIVE,
                    addresses = finalAddresses,
                    defaultAddressId = defaultAddressId,
                    createdAt = now,
                    updatedAt = now,
                    version = 0L,
                )
            user.addDomainEventInternal(
                UserEvent.Created(
                    username = username.value,
                    email = email.value,
                    name = name.value,
                    phoneNumber = phoneNumber?.value,
                    userType = userType.toString(),
                    userId = id,
                    occurredAt = now,
                ),
            )
            return user
        }

        fun reconstitute(
            id: UserId,
            username: Username,
            password: Password,
            email: Email,
            name: Name,
            phoneNumber: PhoneNumber?,
            userType: UserType,
            addresses: List<Address>,
            status: UserStatus,
            createdAt: Instant,
            updatedAt: Instant,
            version: Long,
        ): User {
            val defaultAddrId = addresses.find { it.isDefault }?.addressId
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
                defaultAddressId = defaultAddrId,
                createdAt = createdAt,
                updatedAt = updatedAt,
                version = version,
                eventsInternal = mutableListOf() // 재구성 시에는 이벤트 목록을 비웁니다.
            )
        }
    }
}
