package com.restaurant.application.user.query.dto

import com.restaurant.domain.user.vo.Address
import java.time.LocalDateTime

data class UserProfileDto(
  val id: Long,
  val email: String,
  val name: String,
  val addresses: List<Address> = emptyList(),
  val createdAt: LocalDateTime,
  val updatedAt: LocalDateTime,
)
