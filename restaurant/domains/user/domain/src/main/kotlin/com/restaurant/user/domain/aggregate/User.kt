package com.restaurant.user.domain.aggregate

import com.restaurant.common.domain.aggregate.AggregateRoot
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
import com.fasterxml.jackson.annotation.JsonIgnore

/**
 * User Aggregate Root (Rule 10)
 * 사용자와 관련된 모든 비즈니스 로직을 담당하는 Aggregate Root
 */
data class User private constructor(
    val id: UserId,
    val username: Username,
    val email: Email,
    val password: Password,
    val name: Name,
    val phoneNumber: PhoneNumber?,
    val userType: UserType,
    val status: UserStatus,
    val addresses: List<Address>,
    val createdAt: Instant,
    val updatedAt: Instant,
    @JsonIgnore
    val version: Long = 0L,
) : AggregateRoot() {

    companion object {
        private const val MAX_ADDRESSES = 10

        /**
         * 새로운 User를 생성합니다.
         */
        fun create(
            id: UserId,
            username: Username,
            email: Email,
            password: Password,
            name: Name,
            phoneNumber: PhoneNumber?,
            userType: UserType,
        ): User {
            val now = Instant.now()
            val user = User(
                id = id,
                username = username,
                email = email,
                password = password,
                name = name,
                phoneNumber = phoneNumber,
                userType = userType,
                status = UserStatus.ACTIVE,
                addresses = emptyList(),
                createdAt = now,
                updatedAt = now,
                version = 0L,
            )

            user.addDomainEvent(
                UserEvent.Created(
                    username = username.value,
                    email = email.value,
                    name = name.value,
                    phoneNumber = phoneNumber?.value,
                    userType = userType.name,
                    id = id,
                    occurredAt = now,
                )
            )

            return user
        }

        /**
         * 기존 데이터로부터 User를 재구성합니다.
         */
        fun reconstitute(
            id: UserId,
            username: Username,
            email: Email,
            password: Password,
            name: Name,
            phoneNumber: PhoneNumber?,
            userType: UserType,
            status: UserStatus,
            addresses: List<Address>,
            createdAt: Instant,
            updatedAt: Instant,
            version: Long,
        ): User {
            // 기본 주소 검증
            val defaultAddresses = addresses.filter { it.isDefault }
            if (defaultAddresses.size > 1) {
                throw UserDomainException.Address.MultipleDefaultsOnInit()
            }

            return User(
                id = id,
                username = username,
                email = email,
                password = password,
                name = name,
                phoneNumber = phoneNumber,
                userType = userType,
                status = status,
                addresses = addresses,
                createdAt = createdAt,
                updatedAt = updatedAt,
                version = version,
            )
        }
    }

    /**
     * 사용자 정보를 업데이트합니다.
     */
    fun updateProfile(
        name: Name,
        phoneNumber: PhoneNumber?,
    ): User {
        if (status == UserStatus.WITHDRAWN) {
            throw UserDomainException.User.AlreadyWithdrawn()
        }

        if (this.name == name && this.phoneNumber == phoneNumber) {
            return this
        }

        val updatedUser = copy(
            name = name,
            phoneNumber = phoneNumber,
            updatedAt = Instant.now(),
            version = this.version + 1,
        )

        updatedUser.addDomainEvent(
            UserEvent.ProfileUpdated(
                name = name.value,
                phoneNumber = phoneNumber?.value,
                id = id,
                occurredAt = Instant.now(),
            )
        )

        return updatedUser
    }

    /**
     * 비밀번호를 변경합니다.
     */
    fun changePassword(
        currentPassword: Password,
        newPassword: Password,
    ): User {
        if (status == UserStatus.WITHDRAWN) {
            throw UserDomainException.User.AlreadyWithdrawn()
        }

        if (this.password != currentPassword) {
            throw UserDomainException.User.PasswordMismatch()
        }

        if (this.password == newPassword) {
            return this
        }

        // Validate new password strength/format
        newPassword.validate()

        val updatedUser = copy(
            password = newPassword,
            updatedAt = Instant.now(),
            version = this.version + 1,
        )

        updatedUser.addDomainEvent(
            UserEvent.PasswordChanged(
                id = id,
                occurredAt = Instant.now(),
            )
        )

        return updatedUser
    }

    /**
     * 주소를 추가합니다.
     */
    fun addAddress(address: Address): User {
        if (status == UserStatus.WITHDRAWN) {
            throw UserDomainException.User.AlreadyWithdrawn()
        }

        if (addresses.size >= MAX_ADDRESSES) {
            throw UserDomainException.Address.LimitExceeded(MAX_ADDRESSES)
        }

        if (addresses.any { it.addressId == address.addressId }) {
            throw UserDomainException.Address.DuplicateAddressId(address.addressId.value.toString())
        }

        val updatedAddresses = if (address.isDefault) {
            // 새 주소가 기본 주소라면, 기존 기본 주소들을 비기본으로 변경
            val newAddresses = addresses.map { it.copy(isDefault = false) }.toMutableList()
            newAddresses.add(address)
            newAddresses.toList()
        } else {
            addresses + address
        }

        val updatedUser = copy(
            addresses = updatedAddresses,
            updatedAt = Instant.now(),
            version = this.version + 1,
        )

        updatedUser.addDomainEvent(
            UserEvent.AddressAdded(
                addressId = address.addressId,
                id = id,
                occurredAt = Instant.now(),
            )
        )

        return updatedUser
    }

    /**
     * 주소를 업데이트합니다.
     */
    fun updateAddress(
        addressId: AddressId,
        name: Name,
        streetAddress: String,
        detailAddress: String?,
        city: String,
        state: String,
        country: String,
        zipCode: String,
        isDefault: Boolean,
    ): User {
        if (status == UserStatus.WITHDRAWN) {
            throw UserDomainException.User.AlreadyWithdrawn()
        }

        val existingAddress = addresses.find { it.addressId == addressId }
            ?: throw UserDomainException.Address.NotFound(addressId.value.toString())

        val updatedAddress = existingAddress.copy(
            name = name.value,
            streetAddress = streetAddress,
            detailAddress = detailAddress,
            city = city,
            state = state,
            country = country,
            zipCode = zipCode,
            isDefault = isDefault,
            updatedAt = Instant.now()
        )

        val updatedAddresses = addresses.map { if (it.addressId == addressId) updatedAddress else it }

        if (isDefault && updatedAddresses.count { it.isDefault } > 1) {
            throw UserDomainException.Address.MultipleDefaults()
        }

        val updatedUser = copy(
            addresses = updatedAddresses,
            updatedAt = Instant.now(),
            version = this.version + 1,
        )

        updatedUser.addDomainEvent(
            UserEvent.AddressUpdated(
                id = id,
                addressId = addressId,
                name = name.value,
                streetAddress = streetAddress,
                detailAddress = detailAddress,
                city = city,
                state = state,
                country = country,
                zipCode = zipCode,
                isDefault = isDefault,
                occurredAt = Instant.now(),
            )
        )

        return updatedUser
    }

    /**
     * 주소를 삭제합니다.
     */
    fun deleteAddress(addressId: AddressId): User {
        if (status == UserStatus.WITHDRAWN) {
            throw UserDomainException.User.AlreadyWithdrawn()
        }

        val existingAddress = addresses.find { it.addressId == addressId }
            ?: throw UserDomainException.Address.NotFound(addressId.value.toString())

        val updatedAddresses = addresses.filter { it.addressId != addressId }

        val updatedUser = copy(
            addresses = updatedAddresses,
            updatedAt = Instant.now(),
            version = this.version + 1,
        )

        updatedUser.addDomainEvent(
            UserEvent.AddressDeleted(
                addressId = addressId,
                id = id,
                occurredAt = Instant.now(),
            )
        )

        return updatedUser
    }

    /**
     * 사용자 계정을 비활성화합니다.
     */
    fun deactivate(): User {
        if (status == UserStatus.WITHDRAWN) {
            throw UserDomainException.User.AlreadyWithdrawn()
        }
        if (status == UserStatus.INACTIVE) {
            return this
        }

        val updatedUser = copy(
            status = UserStatus.INACTIVE,
            updatedAt = Instant.now(),
            version = this.version + 1,
        )

        updatedUser.addDomainEvent(
            UserEvent.Deactivated(
                id = id,
                occurredAt = Instant.now(),
            )
        )

        return updatedUser
    }

    /**
     * 사용자 계정을 활성화합니다.
     */
    fun activate(): User {
        if (status == UserStatus.WITHDRAWN) {
            throw UserDomainException.User.AlreadyWithdrawn()
        }
        if (status == UserStatus.ACTIVE) {
            return this
        }

        val updatedUser = copy(
            status = UserStatus.ACTIVE,
            updatedAt = Instant.now(),
            version = this.version + 1,
        )

        updatedUser.addDomainEvent(
            UserEvent.Activated(
                id = id,
                occurredAt = Instant.now(),
            )
        )

        return updatedUser
    }

    /**
     * 사용자 계정을 탈퇴 처리합니다.
     */
    fun withdraw(): User {
        if (status == UserStatus.WITHDRAWN) {
            return this
        }

        val updatedUser = copy(
            status = UserStatus.WITHDRAWN,
            updatedAt = Instant.now(),
            version = this.version + 1,
        )

        updatedUser.addDomainEvent(
            UserEvent.Withdrawn(
                id = id,
                occurredAt = Instant.now(),
            )
        )

        return updatedUser
    }

    /**
     * 기본 주소를 가져옵니다.
     */
    fun getDefaultAddress(): Address? = addresses.find { it.isDefault }

    /**
     * 특정 주소를 가져옵니다.
     */
    fun getAddress(addressId: AddressId): Address? = addresses.find { it.addressId == addressId }

    /**
     * 사용자가 활성 상태인지 확인합니다.
     */
    fun isActive(): Boolean = status == UserStatus.ACTIVE

    /**
     * 사용자가 탈퇴 상태인지 확인합니다.
     */
    fun isWithdrawn(): Boolean = status == UserStatus.WITHDRAWN
}
