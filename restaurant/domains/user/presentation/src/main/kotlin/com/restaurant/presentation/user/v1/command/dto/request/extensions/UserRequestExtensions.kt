package com.restaurant.presentation.user.v1.command.dto.request.extensions

import com.restaurant.application.user.command.ChangePasswordCommand
import com.restaurant.application.user.command.DeleteUserCommand
import com.restaurant.application.user.command.LoginCommand
import com.restaurant.application.user.command.RegisterAddressCommand
import com.restaurant.application.user.command.RegisterUserCommand
import com.restaurant.application.user.command.UpdateAddressCommand
import com.restaurant.application.user.command.UpdateProfileCommand
import com.restaurant.presentation.user.v1.command.dto.request.*

// RegisterUserCommand 변환
fun UserRegisterRequestV1.toCommand(): RegisterUserCommand =
  RegisterUserCommand(
    email = email,
    password = password,
    name = name,
  )

// LoginCommand 변환
fun UserLoginRequestV1.toCommand(): LoginCommand =
  LoginCommand(
    email = email,
    password = password,
  )

// UpdateProfileCommand 변환
fun UserUpdateProfileRequestV1.toCommand(userId: Long): UpdateProfileCommand =
  UpdateProfileCommand(
    userId = userId,
    name = name,
  )

// ChangePasswordCommand 변환
fun UserChangePasswordRequestV1.toCommand(userId: Long): ChangePasswordCommand =
  ChangePasswordCommand(
    userId = userId,
    currentPassword = currentPassword,
    newPassword = newPassword,
  )

// DeleteUserCommand 변환
fun UserDeleteRequestV1.toCommand(userId: Long): DeleteUserCommand =
  DeleteUserCommand(
    userId = userId,
    password = password,
  )

// RegisterAddressCommand 변환
fun UserAddressRegisterRequestV1.toCommand(userId: Long): RegisterAddressCommand =
  RegisterAddressCommand(
    userId = userId,
    street = street,
    detail = detail,
    zipCode = zipCode,
    isDefault = isDefault,
  )

// UpdateAddressCommand 변환
fun UserAddressUpdateRequestV1.toCommand(
  userId: Long,
  addressId: Long,
): UpdateAddressCommand =
  UpdateAddressCommand(
    userId = userId,
    addressId = addressId,
    street = street,
    detail = detail,
    zipCode = zipCode,
    isDefault = isDefault,
  )