package com.restaurant.infrastructure.user.repository

import com.restaurant.domain.user.aggregate.User
import com.restaurant.domain.user.repository.UserRepository
import com.restaurant.domain.user.vo.Email
import com.restaurant.domain.user.vo.UserId
import com.restaurant.infrastructure.user.mapper.toDomain
import com.restaurant.infrastructure.user.mapper.toEntity
import org.springframework.stereotype.Repository

@Repository
class UserRepositoryImpl(
  private val jpaRepository: SpringDataJpaUserRepository,
) : UserRepository {
  override fun save(user: User): User {
    val entity = user.toEntity()

    // 단순하게 entity를 저장하고 변환
    val savedEntity = jpaRepository.save(entity)
    return savedEntity.toDomain()
  }

  override fun findById(id: UserId): User? =
    jpaRepository.findById(id.value).orElse(null)?.let {
      it.toDomain()
    }

  override fun findByEmail(email: Email): User? =
    jpaRepository.findByEmail(email.value)?.let {
      it.toDomain()
    }

  override fun existsByEmail(email: Email): Boolean = jpaRepository.existsByEmail(email.value)

  override fun delete(user: User) {
    val entity = user.toEntity()
    jpaRepository.delete(entity)
  }
}
