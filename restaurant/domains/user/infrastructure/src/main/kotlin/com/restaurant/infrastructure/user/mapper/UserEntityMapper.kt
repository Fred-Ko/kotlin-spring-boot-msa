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
class UserEntityMapper : EntityMapper<User, UserEntity> {
  override fun toEntity(domain: User): UserEntity =
    UserEntity(
      id = domain.id?.value,
      email = domain.email.value,
      password = domain.password.encodedValue,
      name = domain.name.value,
      createdAt = domain.createdAt,
      updatedAt = domain.updatedAt,
    )

  override fun toDomain(entity: UserEntity): User =
    User.reconstitute(
      id = UserId(entity.id!!),
      email = Email(entity.email),
      password = Password.fromEncoded(entity.password),
      name = Name.of(entity.name),
      createdAt = entity.createdAt,
      updatedAt = entity.updatedAt,
    )
}
