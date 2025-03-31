package com.restaurant.application.user.command

data class DeleteAddressCommand(
  val userId: Long,
  val addressId: Long,
)
