package com.restaurant.user.presentation.v1.extensions.command.dto.request

import com.restaurant.user.application.dto.command.ChangePasswordCommand
import com.restaurant.user.application.dto.command.DeleteUserCommand
import com.restaurant.user.application.dto.command.LoginCommand
import com.restaurant.user.application.dto.command.RegisterAddressCommand
import com.restaurant.user.application.dto.command.RegisterUserCommand
import com.restaurant.user.application.dto.command.UpdateAddressCommand
import com.restaurant.user.application.dto.command.UpdateProfileCommand
import com.restaurant.user.presentation.v1.dto.request.ChangePasswordRequestV1
import com.restaurant.user.presentation.v1.dto.request.DeleteUserRequestV1
import com.restaurant.user.presentation.v1.dto.request.LoginRequestV1
import com.restaurant.user.presentation.v1.dto.request.RegisterAddressRequestV1
import com.restaurant.user.presentation.v1.dto.request.RegisterUserRequestV1
import com.restaurant.user.presentation.v1.dto.request.UpdateAddressRequestV1
import com.restaurant.user.presentation.v1.dto.request.UpdateProfileRequestV1
import java.util.UUID

fun RegisterUserRequestV1.toCommand(): RegisterUserCommand =
    RegisterUserCommand(
        email = this.email,
        password = this.password,
        name = this.name,
        username = this.username,
    )

fun LoginRequestV1.toCommand(): LoginCommand =
    LoginCommand(
        email = this.email,
        password = this.password,
    )

fun UpdateProfileRequestV1.toCommand(userId: UUID): UpdateProfileCommand =
    UpdateProfileCommand(
        userId = userId.toString(),
        name = this.name,
        phoneNumber = null,
    )

fun ChangePasswordRequestV1.toCommand(userId: UUID): ChangePasswordCommand =
    ChangePasswordCommand(
        userId = userId.toString(),
        currentPassword = this.currentPassword,
        newPassword = this.newPassword,
    )

fun DeleteUserRequestV1.toCommand(userId: UUID): DeleteUserCommand =
    DeleteUserCommand(
        userId = userId.toString(),
        password = this.currentPassword,
    )

fun RegisterAddressRequestV1.toCommand(userId: String): RegisterAddressCommand =
    RegisterAddressCommand(
        userId = userId,
        street = this.street,
        detail = this.detail ?: "",
        zipCode = this.zipCode,
        isDefault = this.isDefault ?: false,
    )

fun UpdateAddressRequestV1.toCommand(
    userId: String,
    addressId: String,
): UpdateAddressCommand =
    UpdateAddressCommand(
        userId = userId,
        addressId = addressId,
        street = this.street,
        detail = this.detail ?: "",
        zipCode = this.zipCode,
        isDefault = this.isDefault ?: false,
    )
