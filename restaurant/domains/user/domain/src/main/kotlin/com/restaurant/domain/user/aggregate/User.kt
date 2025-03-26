package com.restaurant.domain.user.aggregate

import com.restaurant.domain.user.vo.Email
import com.restaurant.domain.user.vo.Password
import com.restaurant.domain.user.vo.UserId
import java.time.LocalDateTime

class User
  private constructor(
    val id: UserId? = null,
    val email: Email,
    val password: Password,
    val name: String,
    val createdAt: LocalDateTime = LocalDateTime.now(),
    val updatedAt: LocalDateTime = LocalDateTime.now(),
  ) {
    companion object {
      fun create(
        email: Email,
        password: Password,
        name: String,
      ): User = User(email = email, password = password, name = name)

      fun reconstitute(
        id: UserId,
        email: Email,
        password: Password,
        name: String,
        createdAt: LocalDateTime,
        updatedAt: LocalDateTime,
      ): User =
        User(
          id = id,
          email = email,
          password = password,
          name = name,
          createdAt = createdAt,
          updatedAt = updatedAt,
        )
    }

    fun updateProfile(name: String): User =
      User(
        id = this.id,
        email = this.email,
        password = this.password,
        name = name,
        createdAt = this.createdAt,
        updatedAt = LocalDateTime.now(),
      )

    fun changePassword(newPassword: String): User =
      User(
        id = this.id,
        email = this.email,
        password = Password.of(newPassword),
        name = this.name,
        createdAt = this.createdAt,
        updatedAt = LocalDateTime.now(),
      )

    fun checkPassword(rawPassword: String): Boolean = password.matches(rawPassword)
  }
