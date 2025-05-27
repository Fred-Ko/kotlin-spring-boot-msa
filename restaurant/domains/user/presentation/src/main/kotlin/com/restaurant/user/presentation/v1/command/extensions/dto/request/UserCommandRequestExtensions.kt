package com.restaurant.user.presentation.v1.command.extensions.dto.request

import com.restaurant.user.application.command.dto.ChangePasswordCommand
import com.restaurant.user.application.command.dto.DeleteUserCommand
import com.restaurant.user.application.command.dto.LoginCommand
import com.restaurant.user.application.command.dto.RegisterAddressCommand
import com.restaurant.user.application.command.dto.RegisterUserCommand
import com.restaurant.user.application.command.dto.UpdateAddressCommand
import com.restaurant.user.application.command.dto.UpdateProfileCommand
import com.restaurant.user.domain.vo.AddressId
import com.restaurant.user.domain.vo.UserId
import com.restaurant.user.presentation.v1.command.dto.request.ChangePasswordRequestV1
import com.restaurant.user.presentation.v1.command.dto.request.DeleteUserRequestV1
import com.restaurant.user.presentation.v1.command.dto.request.LoginRequestV1
import com.restaurant.user.presentation.v1.command.dto.request.RegisterAddressRequestV1
import com.restaurant.user.presentation.v1.command.dto.request.RegisterUserRequestV1
import com.restaurant.user.presentation.v1.command.dto.request.UpdateAddressRequestV1
import com.restaurant.user.presentation.v1.command.dto.request.UpdateProfileRequestV1

fun RegisterUserRequestV1.toCommand(): RegisterUserCommand =
    RegisterUserCommand(
        email = this.email,
        password = this.password,
        name = this.name,
        username = this.username,
        phoneNumber = this.phoneNumber,
    )

fun LoginRequestV1.toCommand(): LoginCommand =
    LoginCommand(
        email = this.email,
        password = this.password,
    )

fun UpdateProfileRequestV1.toCommand(userId: UserId): UpdateProfileCommand =
    UpdateProfileCommand(
        userId = userId.value.toString(),
        name = this.name,
        phoneNumber = this.phoneNumber,
    )

fun ChangePasswordRequestV1.toCommand(userId: UserId): ChangePasswordCommand =
    ChangePasswordCommand(
        userId = userId.value.toString(),
        currentPassword = this.currentPassword,
        newPassword = this.newPassword,
    )

fun DeleteUserRequestV1.toCommand(userId: UserId): DeleteUserCommand =
    DeleteUserCommand(
        userId = userId.value.toString(),
        password = this.currentPassword,
    )

fun RegisterAddressRequestV1.toCommand(userId: UserId): RegisterAddressCommand =
    RegisterAddressCommand(
        userId = userId.value.toString(),
        name = this.name,
        street = this.street,
        detail = this.detail ?: "",
        city = this.city,
        state = this.state,
        country = this.country,
        zipCode = this.zipCode,
        isDefault = this.isDefault ?: false,
    )

fun UpdateAddressRequestV1.toCommand(
    userId: UserId,
    addressId: AddressId,
): UpdateAddressCommand =
    UpdateAddressCommand(
        userId = userId.value.toString(),
        addressId = addressId.value.toString(),
        name = this.name,
        street = this.street,
        detail = this.detail ?: "",
        city = this.city,
        state = this.state,
        country = this.country,
        zipCode = this.zipCode,
        isDefault = this.isDefault ?: false,
    )
