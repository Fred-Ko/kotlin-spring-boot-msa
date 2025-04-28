package com.restaurant.user.presentation.v1.extensions.command.dto.request

import com.restaurant.user.application.dto.command.ChangePasswordCommand
import com.restaurant.user.application.dto.command.DeleteUserCommand
import com.restaurant.user.application.dto.command.LoginCommand
import com.restaurant.user.application.dto.command.RegisterAddressCommand
import com.restaurant.user.application.dto.command.RegisterUserCommand
import com.restaurant.user.application.dto.command.UpdateAddressCommand
import com.restaurant.user.application.dto.command.UpdateProfileCommand
import com.restaurant.user.domain.aggregate.UserType
import com.restaurant.user.presentation.v1.dto.request.ChangePasswordRequestV1
import com.restaurant.user.presentation.v1.dto.request.DeleteUserRequestV1
import com.restaurant.user.presentation.v1.dto.request.LoginRequestV1
import com.restaurant.user.presentation.v1.dto.request.RegisterAddressRequestV1
import com.restaurant.user.presentation.v1.dto.request.RegisterUserRequestV1
import com.restaurant.user.presentation.v1.dto.request.UpdateAddressRequestV1
import com.restaurant.user.presentation.v1.dto.request.UpdateProfileRequestV1
import java.util.UUID

// RegisterUserRequestV1 -> RegisterUserCommand 변환
fun RegisterUserRequestV1.toCommand(): RegisterUserCommand =
    RegisterUserCommand(
        username = this.username,
        password = this.password,
        email = this.email,
        name = this.name,
        phoneNumber = this.phoneNumber,
        userType = this.userType ?: UserType.CUSTOMER, // Default to CUSTOMER if null
    )

// LoginRequestV1 -> LoginCommand 변환
fun LoginRequestV1.toCommand(): LoginCommand =
    LoginCommand(
        email = this.email,
        password = this.password,
    )

// UpdateProfileRequestV1 -> UpdateProfileCommand 변환
fun UpdateProfileRequestV1.toCommand(userId: UUID): UpdateProfileCommand =
    UpdateProfileCommand(
        userId = userId,
        name = this.name,
        phoneNumber = this.phoneNumber,
    )

// ChangePasswordRequestV1 -> ChangePasswordCommand 변환
fun ChangePasswordRequestV1.toCommand(userId: UUID): ChangePasswordCommand =
    ChangePasswordCommand(
        userId = userId,
        currentPassword = this.currentPassword,
        newPassword = this.newPassword,
    )

// DeleteUserRequestV1 -> DeleteUserCommand 변환
fun DeleteUserRequestV1.toCommand(userId: UUID): DeleteUserCommand =
    DeleteUserCommand(
        userId = userId,
        password = this.password,
    )

// RegisterAddressRequestV1 -> RegisterAddressCommand 변환
fun RegisterAddressRequestV1.toCommand(userId: String): RegisterAddressCommand =
    RegisterAddressCommand(
        userId = userId,
        street = this.street,
        detail = this.detail ?: "",
        zipCode = this.zipCode,
        isDefault = this.isDefault ?: false,
    )

// UpdateAddressRequestV1 -> UpdateAddressCommand 변환
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
