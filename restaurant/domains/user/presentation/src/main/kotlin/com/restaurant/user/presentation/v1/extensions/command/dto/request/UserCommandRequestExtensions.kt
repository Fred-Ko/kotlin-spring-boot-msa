package com.restaurant.user.presentation.v1.extensions.command.dto.request

import com.restaurant.user.application.dto.command.*
import com.restaurant.user.domain.vo.AddressId
import com.restaurant.user.domain.vo.UserId
import com.restaurant.user.presentation.v1.dto.request.*
// import java.util.UUID // Controller에서 UUID를 받지만 Command DTO는 String ID를 사용

fun RegisterUserRequestV1.toCommand(correlationId: String): RegisterUserCommand =
    RegisterUserCommand(
        email = this.email,
        password = this.password,
        name = this.name,
        username = this.username
        // correlationId = correlationId // Command DTO에 필드 추가 필요
    )

fun LoginRequestV1.toCommand(correlationId: String): LoginCommand =
    LoginCommand(
        email = this.email,
        password = this.password
        // correlationId = correlationId // Command DTO에 필드 추가 필요
    )

fun UpdateProfileRequestV1.toCommand(userId: UserId, correlationId: String): UpdateProfileCommand =
    UpdateProfileCommand(
        userId = userId.value.toString(),
        name = this.name,
        phoneNumber = null // Request DTO에 phoneNumber 필드 추가 필요 또는 기본값 처리
        // correlationId = correlationId // Command DTO에 필드 추가 필요
    )

fun ChangePasswordRequestV1.toCommand(userId: UserId, correlationId: String): ChangePasswordCommand =
    ChangePasswordCommand(
        userId = userId.value.toString(),
        currentPassword = this.currentPassword,
        newPassword = this.newPassword
        // correlationId = correlationId // Command DTO에 필드 추가 필요
    )

fun DeleteUserRequestV1.toCommand(userId: UserId, correlationId: String): DeleteUserCommand =
    DeleteUserCommand(
        userId = userId.value.toString(),
        password = this.currentPassword
        // correlationId = correlationId // Command DTO에 필드 추가 필요
    )

// RegisterAddressRequestV1에는 name, city, state, country 필드가 없음.
// Command DTO의 해당 필드들은 임시로 빈 문자열 또는 기본값으로 채우거나, nullable로 변경 필요.
fun RegisterAddressRequestV1.toCommand(userId: UserId, correlationId: String): RegisterAddressCommand =
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
        // correlationId = correlationId // Command DTO에 필드 추가 필요
    )

// UpdateAddressRequestV1에는 name, city, state, country 필드가 없음.
fun UpdateAddressRequestV1.toCommand(
    userId: UserId,
    addressId: AddressId,
    correlationId: String
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
        // correlationId = correlationId // Command DTO에 필드 추가 필요
    )
