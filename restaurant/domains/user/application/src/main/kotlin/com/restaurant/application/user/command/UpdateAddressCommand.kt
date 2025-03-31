package com.restaurant.application.user.command

data class UpdateAddressCommand(
  val userId: Long,
  val addressId: Long,
  val street: String,
  val detail: String,
  val zipCode: String,
  val isDefault: Boolean,
)
