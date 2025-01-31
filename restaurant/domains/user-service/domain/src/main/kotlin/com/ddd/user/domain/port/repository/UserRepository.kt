package com.ddd.user.domain.port.repository

import com.ddd.user.domain.model.aggregate.User
import com.ddd.user.domain.model.vo.Email
import java.util.UUID
import org.springframework.data.domain.Page
import org.springframework.data.domain.PageRequest

interface UserRepository {
    fun save(user: User): User
    fun findById(id: UUID): User?
    fun findAll(pageRequest: PageRequest): Page<User>
    fun findByEmail(email: Email): User?
    fun delete(user: User)
    fun existsByEmail(email: Email): Boolean
}
