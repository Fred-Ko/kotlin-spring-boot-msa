package com.restaurant.user.infrastructure.persistence.extensions

import com.restaurant.user.domain.entity.Address
import com.restaurant.user.domain.vo.AddressId
// UserId는 Address.reconstitute에서 사용하지 않는다고 가정하고 제거
import com.restaurant.common.infrastructure.persistence.entity.BaseEntity
import com.restaurant.user.infrastructure.persistence.entity.AddressEntity
// UserEntity import는 toEntity에서 사용하지 않으므로 제거 가능 (만약 AddressEntity가 UserEntity를 참조하지 않는다면)
// import com.restaurant.common.infrastructure.persistence.entity.BaseEntity.UserEntity
import java.time.Instant
import java.util.UUID

// Address.reconstitute에서 userId를 사용하지 않는다고 가정
fun AddressEntity.toDomain(): Address {
    return Address.reconstitute(
        addressId = AddressId.of(this.domainId),
        name = this.name,
        streetAddress = this.streetAddress,
        detailAddress = this.detailAddress,
        city = this.city,
        state = this.state,
        country = this.country,
        zipCode = this.zipCode,
        isDefault = this.isDefault,
        createdAt = this.createdAt ?: java.time.Instant.now(),
        updatedAt = this.updatedAt ?: java.time.Instant.now(),
        version = this.version
    )
}

// AddressEntity 생성 시 UserEntity를 받지 않음
// UserEntity의 addresses 컬렉션에 추가될 때 관계가 설정됨
fun Address.toEntity(): AddressEntity {
    return AddressEntity(
        // id는 DB 자동 생성
        domainId = this.addressId.value,
        name = this.name,
        streetAddress = this.streetAddress,
        detailAddress = this.detailAddress,
        city = this.city,
        state = this.state,
        country = this.country,
        zipCode = this.zipCode,
        isDefault = this.isDefault,
        version = this.version      // Address 도메인 객체에 해당 필드가 있다고 가정
        // user 필드는 UserEntity에서 AddressEntity를 추가하면서 설정
    )
}
