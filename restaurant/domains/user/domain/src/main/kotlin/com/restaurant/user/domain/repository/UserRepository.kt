package com.restaurant.user.domain.repository

import com.restaurant.user.domain.aggregate.User
import com.restaurant.user.domain.vo.Email
import com.restaurant.user.domain.vo.UserId
import com.restaurant.user.domain.vo.Username

interface UserRepository {
    fun findById(id: UserId): User?

    fun findByUsername(username: Username): User?

    fun findByEmail(email: Email): User? // findByEmail 메서드 추가
    fun existsByUsername(username: Username): Boolean

    fun existsByEmail(email: Email): Boolean

    fun save(user: User): User

    fun delete(user: User)
}
