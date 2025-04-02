package com.restaurant.presentation.user.v1.extensions.request

import com.restaurant.application.user.command.ChangePasswordCommand
import com.restaurant.application.user.command.DeleteAddressCommand
import com.restaurant.application.user.command.DeleteUserCommand
import com.restaurant.application.user.command.LoginCommand
import com.restaurant.application.user.command.RegisterAddressCommand
import com.restaurant.application.user.command.RegisterUserCommand
import com.restaurant.application.user.command.UpdateAddressCommand
import com.restaurant.application.user.command.UpdateProfileCommand
import com.restaurant.application.user.query.GetUserProfileQuery
import com.restaurant.presentation.user.v1.dto.request.ChangePasswordRequestV1
import com.restaurant.presentation.user.v1.dto.request.DeleteAddressRequestV1
import com.restaurant.presentation.user.v1.dto.request.DeleteUserRequestV1
import com.restaurant.presentation.user.v1.dto.request.LoginRequestV1
import com.restaurant.presentation.user.v1.dto.request.RegisterAddressRequestV1
import com.restaurant.presentation.user.v1.dto.request.RegisterUserRequestV1
import com.restaurant.presentation.user.v1.dto.request.UpdateAddressRequestV1
import com.restaurant.presentation.user.v1.dto.request.UpdateProfileRequestV1

// RegisterUserRequestV1 -> RegisterUserCommand 변환
fun RegisterUserRequestV1.toCommand(): RegisterUserCommand =
    RegisterUserCommand(
        email = this.email,
        password = this.password,
        name = this.name,
    )

// LoginRequestV1 -> LoginCommand 변환
fun LoginRequestV1.toCommand(): LoginCommand =
    LoginCommand(
        email = this.email,
        password = this.password,
    )

// UpdateProfileRequestV1 -> UpdateProfileCommand 변환
fun UpdateProfileRequestV1.toCommand(userId: Long): UpdateProfileCommand =
    UpdateProfileCommand(
        userId = userId,
        name = this.name,
    )

// ChangePasswordRequestV1 -> ChangePasswordCommand 변환
fun ChangePasswordRequestV1.toCommand(userId: Long): ChangePasswordCommand =
    ChangePasswordCommand(
        userId = userId,
        currentPassword = this.currentPassword,
        newPassword = this.newPassword,
    )

// DeleteUserRequestV1 -> DeleteUserCommand 변환
fun DeleteUserRequestV1.toCommand(userId: Long): DeleteUserCommand =
    DeleteUserCommand(
        userId = userId,
        password = this.password,
    )

// RegisterAddressRequestV1 -> RegisterAddressCommand 변환
fun RegisterAddressRequestV1.toCommand(userId: Long): RegisterAddressCommand =
    RegisterAddressCommand(
        userId = userId,
        street = this.street,
        detail = this.detail,
        zipCode = this.zipCode,
        isDefault = this.isDefault,
    )

// UpdateAddressRequestV1 -> UpdateAddressCommand 변환
fun UpdateAddressRequestV1.toCommand(
    userId: Long,
    addressId: Long,
): UpdateAddressCommand =
    UpdateAddressCommand(
        userId = userId,
        addressId = addressId,
        street = this.street,
        detail = this.detail,
        zipCode = this.zipCode,
        isDefault = this.isDefault,
    )

// DeleteAddressRequestV1 -> DeleteAddressCommand 변환
fun DeleteAddressRequestV1.toCommand(userId: Long): DeleteAddressCommand =
    DeleteAddressCommand(
        userId = userId,
        addressId = this.addressId,
    )

// Long -> GetUserProfileQuery 변환
fun Long.toGetUserProfileQuery(): GetUserProfileQuery =
    GetUserProfileQuery(
        userId = this,
    )
