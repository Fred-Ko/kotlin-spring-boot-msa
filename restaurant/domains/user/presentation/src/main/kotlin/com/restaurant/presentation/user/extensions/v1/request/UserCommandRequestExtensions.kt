package com.restaurant.presentation.user.extensions.v1.request

import com.restaurant.application.user.command.ChangePasswordCommand
import com.restaurant.application.user.command.DeleteUserCommand
import com.restaurant.application.user.command.LoginCommand
import com.restaurant.application.user.command.RegisterUserCommand
import com.restaurant.application.user.command.UpdateProfileCommand
import com.restaurant.presentation.user.v1.command.dto.request.ChangePasswordRequestV1
import com.restaurant.presentation.user.v1.command.dto.request.DeleteUserRequestV1
import com.restaurant.presentation.user.v1.command.dto.request.LoginRequestV1
import com.restaurant.presentation.user.v1.command.dto.request.RegisterUserRequestV1
import com.restaurant.presentation.user.v1.command.dto.request.UpdateProfileRequestV1

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
fun UpdateProfileRequestV1.toCommand(userId: String): UpdateProfileCommand =
    UpdateProfileCommand(
        userId = userId,
        name = this.name,
    )

// ChangePasswordRequestV1 -> ChangePasswordCommand 변환
fun ChangePasswordRequestV1.toCommand(userId: String): ChangePasswordCommand =
    ChangePasswordCommand(
        userId = userId,
        currentPassword = this.currentPassword,
        newPassword = this.newPassword,
    )

// DeleteUserRequestV1 -> DeleteUserCommand 변환
fun DeleteUserRequestV1.toCommand(userId: String): DeleteUserCommand =
    DeleteUserCommand(
        userId = userId,
        password = this.currentPassword,
    )
