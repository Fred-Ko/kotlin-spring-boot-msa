package com.restaurant.user.presentation.v1.extensions.query.dto.response

import com.restaurant.user.application.dto.query.AddressDto
import com.restaurant.user.application.dto.query.UserProfileDto
import com.restaurant.user.presentation.v1.api.UserAddressController
import com.restaurant.user.presentation.v1.api.UserController
import com.restaurant.user.presentation.v1.api.UserQueryController
import com.restaurant.user.presentation.v1.dto.request.ChangePasswordRequestV1
import com.restaurant.user.presentation.v1.dto.request.DeleteUserRequestV1
import com.restaurant.user.presentation.v1.dto.request.RegisterAddressRequestV1
import com.restaurant.user.presentation.v1.dto.request.UpdateProfileRequestV1
import com.restaurant.user.presentation.v1.dto.response.AddressResponseV1
import com.restaurant.user.presentation.v1.dto.response.UserProfileResponseV1
import org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.linkTo
import org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.methodOn
import java.util.UUID

fun UserProfileDto.toResponseV1(correlationId: String): UserProfileResponseV1 {
    val userIdAsUuid = UUID.fromString(this.id)
    val response =
        UserProfileResponseV1(
            id = this.id,
            email = this.email,
            username = this.username,
            name = this.name,
            phoneNumber = this.phoneNumber,
            userType = this.userType,
            addresses = this.addresses.map { addressDto: UserProfileDto.AddressDto -> addressDto.toResponseV1() },
            createdAt = this.createdAt,
            updatedAt = this.updatedAt,
            status = this.status,
            version = this.version,
        )

    response.add(
        linkTo(methodOn(UserQueryController::class.java).getUserProfile(userIdAsUuid, correlationId)).withSelfRel(),
        linkTo(
            methodOn(
                UserController::class.java,
            ).updateProfile(
                userIdAsUuid,
                UpdateProfileRequestV1(name = "dummyName"),
                correlationId,
            ),
        ).withRel("update-profile"),
        linkTo(
            methodOn(
                UserController::class.java,
            ).changePassword(
                userIdAsUuid,
                ChangePasswordRequestV1(currentPassword = "dummyPassword", newPassword = "dummyNewPassword"),
                correlationId,
            ),
        ).withRel("change-password"),
        linkTo(
            methodOn(UserController::class.java).deleteUser(
                userIdAsUuid,
                DeleteUserRequestV1(currentPassword = "dummyPassword"),
                correlationId,
            ),
        ).withRel("delete-user"),
        linkTo(
            methodOn(
                UserAddressController::class.java,
            ).registerAddress(
                userIdAsUuid,
                RegisterAddressRequestV1(
                    street = "dummy street",
                    detail = "dummy detail",
                    zipCode = "00000",
                    isDefault = false,
                ),
                correlationId,
            ),
        ).withRel("register-address"),
    )
    return response
}

fun UserProfileDto.AddressDto.toResponseV1(): AddressResponseV1 =
    AddressResponseV1(
        id = this.id,
        street = this.street ?: "",
        detail = this.detail ?: "",
        zipCode = this.zipCode ?: "",
        isDefault = this.isDefault,
    )
