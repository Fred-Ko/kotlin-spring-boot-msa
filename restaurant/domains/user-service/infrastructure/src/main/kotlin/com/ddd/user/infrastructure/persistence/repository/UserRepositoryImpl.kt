package com.ddd.user.infrastructure.persistence.repository

import com.ddd.user.domain.model.aggregate.User
import com.ddd.user.domain.model.vo.Email
import com.ddd.user.domain.repository.UserRepository
import com.ddd.user.infrastructure.persistence.entity.UserEntity
import java.util.UUID
import org.springframework.data.domain.Page
import org.springframework.data.domain.Pageable
import org.springframework.stereotype.Repository

@Repository
class UserRepositoryImpl(private val userJpaRepository: UserJpaRepository) : UserRepository {

    override fun save(user: User): User {
        val entity = UserEntity.from(user)
        return userJpaRepository.save(entity).toDomain()
    }

    override fun findById(id: UUID): User? {
        return userJpaRepository.findById(id).orElse(null)?.toDomain()
    }

    override fun findByEmail(email: Email): User? {
        return userJpaRepository.findByEmail(email.value)?.toDomain()
    }

    override fun findAll(pageable: Pageable): Page<User> {
        return userJpaRepository.findAll(pageable).map { it.toDomain() }
    }

    override fun existsByEmail(email: Email): Boolean {
        return userJpaRepository.existsByEmail(email.value)
    }

    override fun delete(user: User) {
        userJpaRepository.deleteById(user.id)
    }
}
