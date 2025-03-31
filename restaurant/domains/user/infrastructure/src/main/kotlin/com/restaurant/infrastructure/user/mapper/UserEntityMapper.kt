package com.restaurant.infrastructure.user.mapper

import com.restaurant.common.infrastructure.mapper.EntityMapper
import com.restaurant.domain.user.aggregate.User
import com.restaurant.domain.user.vo.Email
import com.restaurant.domain.user.vo.Name
import com.restaurant.domain.user.vo.Password
import com.restaurant.domain.user.vo.UserId
import com.restaurant.infrastructure.user.entity.UserEntity
import org.springframework.stereotype.Component

@Component
class UserEntityMapper(
  private val addressEntityMapper: AddressEntityMapper,
) : EntityMapper<User, UserEntity> {
  override fun toEntity(domain: User): UserEntity {
    val entity =
      UserEntity(
        id = domain.id?.value,
        email = domain.email.value,
        password = domain.password.encodedValue,
        name = domain.name.value,
        createdAt = domain.createdAt,
        updatedAt = domain.updatedAt,
      )

    // 주소 목록 변환 처리는 별도로 수행해야 함 (순환 참조 문제 때문에)
    // 이 메서드 호출 후 별도 처리 필요

    return entity
  }

  override fun toDomain(entity: UserEntity): User {
    val addresses = entity.addresses.map { addressEntityMapper.toDomain(it) }

    return User.reconstitute(
      id = UserId(entity.id!!),
      email = Email(entity.email),
      password = Password.fromEncoded(entity.password),
      name = Name.of(entity.name),
      addresses = addresses,
      createdAt = entity.createdAt,
      updatedAt = entity.updatedAt,
    )
  }

  // 주소 목록 처리를 위한 추가 메서드
  fun syncAddresses(
    user: User,
    entity: UserEntity,
  ) {
    // 기존 주소 목록 초기화
    entity.addresses.clear()

    // 새 주소 추가
    user.addresses.forEach { address ->
      val addressEntity = addressEntityMapper.toEntity(address, entity)
      entity.addresses.add(addressEntity)
    }
  }
}
