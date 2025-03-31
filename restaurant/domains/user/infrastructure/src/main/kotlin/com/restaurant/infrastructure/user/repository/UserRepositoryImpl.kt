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
  private val userEntityMapper: UserEntityMapper,
) : UserRepository {
  override fun save(user: User): User {
    val entity = userEntityMapper.toEntity(user)

    // User 엔티티 저장
    val savedEntity =
      if (entity.id == null) {
        // 새로운 유저 저장
        val newEntity = jpaRepository.save(entity)

        // 저장된 엔티티에 주소 추가
        userEntityMapper.syncAddresses(user, newEntity)
        jpaRepository.save(newEntity)
      } else {
        // 기존 유저 업데이트
        val existingEntity =
          jpaRepository.findById(entity.id).orElseThrow {
            IllegalStateException("업데이트하려는 사용자를 찾을 수 없습니다: ${entity.id}")
          }

        // 주소 동기화
        userEntityMapper.syncAddresses(user, entity)
        jpaRepository.save(entity)
      }

    return userEntityMapper.toDomain(savedEntity)
  }

  override fun findById(id: UserId): User? =
    jpaRepository.findById(id.value).orElse(null)?.let {
      userEntityMapper.toDomain(it)
    }

  override fun findByEmail(email: Email): User? =
    jpaRepository.findByEmail(email.value)?.let {
      userEntityMapper.toDomain(it)
    }

  override fun existsByEmail(email: Email): Boolean = jpaRepository.existsByEmail(email.value)

  override fun delete(user: User) {
    val entity = userEntityMapper.toEntity(user)
    jpaRepository.delete(entity)
  }
}
