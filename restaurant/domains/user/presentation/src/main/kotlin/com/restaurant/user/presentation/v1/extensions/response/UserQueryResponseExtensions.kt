package com.restaurant.user.presentation.v1.extensions.response

import com.restaurant.user.application.dto.query.UserProfileDto
import com.restaurant.user.presentation.v1.controller.UserController
import com.restaurant.user.presentation.v1.controller.UserQueryController
import com.restaurant.user.presentation.v1.dto.response.AddressResponseV1
import com.restaurant.user.presentation.v1.dto.response.UserProfileResponseV1
import org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.linkTo
import org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.methodOn
import java.util.UUID

// UserProfileDto -> UserProfileResponseV1 변환
fun UserProfileDto.toResponseV1(): UserProfileResponseV1 {
    val userId = this.id // Assuming UserProfileDto has 'id' which is the UUID string
    val userUuid = UUID.fromString(userId)

    // Use correct Controller classes for links
    val selfLink = linkTo(methodOn(UserQueryController::class.java).getUserProfile(userId)).withSelfRel()
    // Assuming addresses are handled by UserAddressController or UserQueryController
    // val addressesLink = linkTo(methodOn(UserAddressController::class.java).getUserAddresses(userUuid)).withRel("addresses")
    val updateProfileLink =
        linkTo(methodOn(UserController::class.java).updateProfile(userUuid, null)).withRel("update-profile")
    val changePasswordLink =
        linkTo(methodOn(UserController::class.java).changePassword(userUuid, null)).withRel("change-password")
    val deleteUserLink = linkTo(methodOn(UserController::class.java).deleteUser(userUuid, null)).withRel("delete-user")

    return UserProfileResponseV1(
        id = this.id,
        username = this.username,
        email = this.email,
        name = this.name,
        phoneNumber = this.phoneNumber,
        userType = this.userType,
        status = this.userStatus, // Map userStatus field added in Query DTO
        createdAt = this.createdAt, // Keep as Instant or format as needed
        updatedAt = this.updatedAt, // Keep as Instant or format as needed
        version = this.version, // Map version field added in Query DTO
        addresses = this.addresses.map { it.toResponseV1() }, // Use correct function name
    ).apply {
        // Add relevant links
        add(selfLink, updateProfileLink, changePasswordLink, deleteUserLink)
        // if (addressesLink != null) add(addressesLink) // Add address link if implemented
    }
}

// UserProfileDto.AddressDto -> AddressResponseV1 변환
fun UserProfileDto.AddressDto.toResponseV1(): AddressResponseV1 {
    // Add links if needed, e.g., link to update/delete this specific address
    // val updateLink = linkTo(methodOn(UserAddressController::class.java).updateAddress(userId, addressId, null)).withRel("update")
    // val deleteLink = linkTo(methodOn(UserAddressController::class.java).deleteAddress(userId, addressId)).withRel("delete")
    return AddressResponseV1(
        id = this.id, // Use correct field name 'id' from DTO
        street = this.street,
        detail = this.detail,
        zipCode = this.zipCode,
        isDefault = this.isDefault,
    ) // .apply { add(updateLink, deleteLink) }
}

// List extension (can be useful)
fun List<UserProfileDto.AddressDto>.toResponseV1(): List<AddressResponseV1> = map { it.toResponseV1() }
