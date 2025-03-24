package com.restaurant.infrastructure.user.repository

import com.restaurant.domain.user.aggregate.User
import com.restaurant.domain.user.repository.UserRepository
import com.restaurant.domain.user.vo.Email
import com.restaurant.domain.user.vo.UserId
import com.restaurant.infrastructure.user.mapper.UserEntityMapper
import org.springframework.stereotype.Repository

@Repository
class UserRepositoryImpl(
        private val jpaRepository: SpringDataJpaUserRepository,
        private val userEntityMapper: UserEntityMapper
) : UserRepository {

    override fun save(user: User): User {
        val entity = userEntityMapper.toEntity(user)
        val savedEntity = jpaRepository.save(entity)
        return userEntityMapper.toDomain(savedEntity)
    }

    override fun findById(id: UserId): User? {
        return jpaRepository.findById(id.value).orElse(null)?.let { userEntityMapper.toDomain(it) }
    }

    override fun findByEmail(email: Email): User? {
        return jpaRepository.findByEmail(email.value)?.let { userEntityMapper.toDomain(it) }
    }

    override fun existsByEmail(email: Email): Boolean {
        return jpaRepository.existsByEmail(email.value)
    }

    override fun delete(user: User) {
        val entity = userEntityMapper.toEntity(user)
        jpaRepository.delete(entity)
    }
}
