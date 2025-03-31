package com.restaurant.infrastructure.user.mapper

import com.restaurant.domain.user.vo.Address
import com.restaurant.infrastructure.user.entity.AddressEntity
import com.restaurant.infrastructure.user.entity.UserEntity
import org.springframework.stereotype.Component

@Component
class AddressEntityMapper {
  fun toEntity(
    domain: Address,
    userEntity: UserEntity,
  ): AddressEntity =
    AddressEntity(
      id = domain.id,
      user = userEntity,
      street = domain.street,
      detail = domain.detail,
      zipCode = domain.zipCode,
      isDefault = domain.isDefault,
    )

  fun toDomain(entity: AddressEntity): Address =
    Address.reconstitute(
      id = entity.id!!,
      street = entity.street,
      detail = entity.detail,
      zipCode = entity.zipCode,
      isDefault = entity.isDefault,
    )
}
