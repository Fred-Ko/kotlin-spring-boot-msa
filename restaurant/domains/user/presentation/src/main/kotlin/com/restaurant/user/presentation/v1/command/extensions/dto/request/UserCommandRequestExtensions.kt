package com.restaurant.user.presentation.v1.command.extensions.dto.request

import com.restaurant.user.application.dto.command.RegisterUserCommand
import com.restaurant.user.application.dto.command.LoginCommand
import com.restaurant.user.application.dto.command.UpdateProfileCommand
import com.restaurant.user.application.dto.command.ChangePasswordCommand
import com.restaurant.user.application.dto.command.DeleteUserCommand
import com.restaurant.user.application.dto.command.RegisterAddressCommand
import com.restaurant.user.application.dto.command.UpdateAddressCommand
import com.restaurant.user.domain.vo.AddressId
import com.restaurant.user.domain.vo.UserId
import com.restaurant.user.presentation.v1.command.dto.request.RegisterUserRequestV1
import com.restaurant.user.presentation.v1.command.dto.request.LoginRequestV1
import com.restaurant.user.presentation.v1.command.dto.request.UpdateProfileRequestV1
import com.restaurant.user.presentation.v1.command.dto.request.ChangePasswordRequestV1
import com.restaurant.user.presentation.v1.command.dto.request.DeleteUserRequestV1
import com.restaurant.user.presentation.v1.command.dto.request.RegisterAddressRequestV1
import com.restaurant.user.presentation.v1.command.dto.request.UpdateAddressRequestV1


fun RegisterUserRequestV1.toCommand(): RegisterUserCommand =
    RegisterUserCommand(
        email = this.email,
        password = this.password,
        name = this.name,
        username = this.username
    )

fun LoginRequestV1.toCommand(): LoginCommand =
    LoginCommand(
        email = this.email,
        password = this.password
    )

fun UpdateProfileRequestV1.toCommand(userId: UserId): UpdateProfileCommand =
    UpdateProfileCommand(
        userId = userId.value.toString(),
        name = this.name,
        phoneNumber = null
    )

fun ChangePasswordRequestV1.toCommand(userId: UserId): ChangePasswordCommand =
    ChangePasswordCommand(
        userId = userId.value.toString(),
        currentPassword = this.currentPassword,
        newPassword = this.newPassword
    )

fun DeleteUserRequestV1.toCommand(userId: UserId): DeleteUserCommand =
    DeleteUserCommand(
        userId = userId.value.toString(),
        password = this.currentPassword
    )

// RegisterAddressRequestV1에는 name, city, state, country 필드가 없음.
// Command DTO의 해당 필드들은 임시로 빈 문자열 또는 기본값으로 채우거나, nullable로 변경 필요.
fun RegisterAddressRequestV1.toCommand(userId: UserId): RegisterAddressCommand =
    RegisterAddressCommand(
        userId = userId.value.toString(),
        name = "Default Address Name", // 임시값, Request DTO에 필드 추가 필요
        street = this.street,
        detail = this.detail ?: "",
        city = "Default City", // 임시값, Request DTO에 필드 추가 필요
        state = "Default State", // 임시값, Request DTO에 필드 추가 필요
        country = "Default Country", // 임시값, Request DTO에 필드 추가 필요
        zipCode = this.zipCode,
        isDefault = this.isDefault ?: false
        
    )

// UpdateAddressRequestV1에는 name, city, state, country 필드가 없음.
fun UpdateAddressRequestV1.toCommand(
    userId: UserId,
    addressId: AddressId
): UpdateAddressCommand =
    UpdateAddressCommand(
        userId = userId.value.toString(),
        addressId = addressId.value.toString(),
        name = "Default Address Name", // 임시값, Request DTO에 필드 추가 필요
        street = this.street,
        detail = this.detail ?: "",
        city = "Default City", // 임시값, Request DTO에 필드 추가 필요
        state = "Default State", // 임시값, Request DTO에 필드 추가 필요
        country = "Default Country", // 임시값, Request DTO에 필드 추가 필요
        zipCode = this.zipCode,
        isDefault = this.isDefault ?: false
    )
