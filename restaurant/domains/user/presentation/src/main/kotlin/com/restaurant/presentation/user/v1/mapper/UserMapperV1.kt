package com.restaurant.presentation.user.v1.mapper

import com.restaurant.application.user.command.ChangePasswordCommand
import com.restaurant.application.user.command.DeleteUserCommand
import com.restaurant.application.user.command.LoginCommand
import com.restaurant.application.user.command.RegisterUserCommand
import com.restaurant.application.user.command.UpdateProfileCommand
import com.restaurant.application.user.query.dto.UserProfileDto
import com.restaurant.presentation.user.v1.command.dto.request.UserChangePasswordRequestV1
import com.restaurant.presentation.user.v1.command.dto.request.UserDeleteRequestV1
import com.restaurant.presentation.user.v1.command.dto.request.UserLoginRequestV1
import com.restaurant.presentation.user.v1.command.dto.request.UserRegisterRequestV1
import com.restaurant.presentation.user.v1.command.dto.request.UserUpdateProfileRequestV1
import com.restaurant.presentation.user.v1.query.dto.response.UserProfileResponseV1
import org.mapstruct.Mapper
import org.mapstruct.Mapping

@Mapper(componentModel = "spring")
interface UserMapperV1 {
  fun toRegisterUserCommand(request: UserRegisterRequestV1): RegisterUserCommand

  fun toLoginCommand(request: UserLoginRequestV1): LoginCommand

  @Mapping(target = "userId", source = "userId")
  fun toUpdateProfileCommand(
    userId: Long,
    request: UserUpdateProfileRequestV1,
  ): UpdateProfileCommand

  @Mapping(target = "userId", source = "userId")
  fun toChangePasswordCommand(
    userId: Long,
    request: UserChangePasswordRequestV1,
  ): ChangePasswordCommand

  @Mapping(target = "userId", source = "userId")
  fun toDeleteUserCommand(
    userId: Long,
    request: UserDeleteRequestV1,
  ): DeleteUserCommand

  fun toUserProfileResponseV1(dto: UserProfileDto): UserProfileResponseV1
}
