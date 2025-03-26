package com.restaurant.application.user.command

data class DeleteUserCommand(
  val userId: Long,
  val password: String,
)
