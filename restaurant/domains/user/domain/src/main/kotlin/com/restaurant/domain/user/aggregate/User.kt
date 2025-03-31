package com.restaurant.domain.user.aggregate

import com.restaurant.domain.user.entity.Address
import com.restaurant.domain.user.vo.Email
import com.restaurant.domain.user.vo.Name
import com.restaurant.domain.user.vo.Password
import com.restaurant.domain.user.vo.UserId
import java.time.LocalDateTime

class User
private constructor(
        val id: UserId? = null,
        val email: Email,
        val password: Password,
        val name: Name,
        val addresses: List<Address> = emptyList(),
        val createdAt: LocalDateTime = LocalDateTime.now(),
        val updatedAt: LocalDateTime = LocalDateTime.now(),
) {
  companion object {
    fun create(
            email: Email,
            password: Password,
            name: Name,
    ): User = User(email = email, password = password, name = name)

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

  fun updateProfile(name: Name): User =
          User(
                  id = this.id,
                  email = this.email,
                  password = this.password,
                  name = name,
                  addresses = this.addresses,
                  createdAt = this.createdAt,
                  updatedAt = LocalDateTime.now(),
          )

  fun changePassword(newPassword: String): User =
          User(
                  id = this.id,
                  email = this.email,
                  password = Password.of(newPassword),
                  name = this.name,
                  addresses = this.addresses,
                  createdAt = this.createdAt,
                  updatedAt = LocalDateTime.now(),
          )

  fun checkPassword(rawPassword: String): Boolean = password.matches(rawPassword)

  fun addAddress(address: Address): User {
    val newAddresses =
            if (address.isDefault) {
              // 기본 주소로 설정된 경우, 기존의 기본 주소를 일반 주소로 변경
              addresses.map { it.update(isDefault = false) } + address
            } else {
              // 기본 주소가 하나도 없는 경우, 첫 번째 주소는 기본 주소로 설정
              if (addresses.isEmpty()) {
                listOf(address.update(isDefault = true))
              } else {
                addresses + address
              }
            }

    return User(
            id = this.id,
            email = this.email,
            password = this.password,
            name = this.name,
            addresses = newAddresses,
            createdAt = this.createdAt,
            updatedAt = LocalDateTime.now(),
    )
  }

  fun updateAddress(
          addressId: Long,
          updatedAddress: Address,
  ): User {
    // 업데이트할 주소 찾기
    val existingAddress =
            addresses.find { it.id == addressId }
                    ?: throw IllegalArgumentException("주소 ID ${addressId}를 찾을 수 없습니다.")

    val newAddresses =
            if (updatedAddress.isDefault) {
              // 기본 주소로 설정하는 경우, 다른 모든 주소를 기본이 아니게 변경
              addresses.map {
                when {
                  it.id == addressId -> updatedAddress
                  else -> it.update(isDefault = false)
                }
              }
            } else {
              // 기본 주소를 일반 주소로 변경하는 경우, 다른 주소를 기본으로 설정
              val currentDefault = addresses.find { it.isDefault }
              val isCurrentAddressDefault = existingAddress.isDefault

              if (isCurrentAddressDefault && currentDefault?.id == addressId) {
                // 현재 수정하려는 주소가 기본 주소이고, 기본을 해제하는 경우
                val addressesWithoutDefault =
                        addresses.map { if (it.id == addressId) updatedAddress else it }

                // 새로운 기본 주소 설정 (첫 번째 다른 주소)
                val otherAddress = addressesWithoutDefault.find { it.id != addressId }
                if (otherAddress != null) {
                  addressesWithoutDefault.map {
                    if (it.id == otherAddress.id) it.update(isDefault = true) else it
                  }
                } else {
                  addressesWithoutDefault
                }
              } else {
                // 일반적인 업데이트
                addresses.map { if (it.id == addressId) updatedAddress else it }
              }
            }

    return User(
            id = this.id,
            email = this.email,
            password = this.password,
            name = this.name,
            addresses = newAddresses,
            createdAt = this.createdAt,
            updatedAt = LocalDateTime.now(),
    )
  }

  fun removeAddress(addressId: Long): User {
    // 삭제할 주소 찾기
    val existingAddress =
            addresses.find { it.id == addressId }
                    ?: throw IllegalArgumentException("주소 ID ${addressId}를 찾을 수 없습니다.")

    val remainingAddresses = addresses.filter { it.id != addressId }

    // 삭제된 주소가 기본 주소였으면 다른 주소를 기본으로 설정
    val newAddresses =
            if (existingAddress.isDefault && remainingAddresses.isNotEmpty()) {
              val firstAddress = remainingAddresses.first()
              remainingAddresses.map {
                if (it.id == firstAddress.id) it.update(isDefault = true) else it
              }
            } else {
              remainingAddresses
            }

    return User(
            id = this.id,
            email = this.email,
            password = this.password,
            name = this.name,
            addresses = newAddresses,
            createdAt = this.createdAt,
            updatedAt = LocalDateTime.now(),
    )
  }
}
