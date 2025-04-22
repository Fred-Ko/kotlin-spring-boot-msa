package com.restaurant.infrastructure.user.extensions

import com.restaurant.domain.user.model.Address
import com.restaurant.domain.user.vo.AddressId
import com.restaurant.infrastructure.user.entity.AddressEntity

// AddressEntity -> Address 변환
fun AddressEntity.toDomain(): Address {
    // val id = this.id ?: throw IllegalStateException("영속화된 AddressEntity의 ID는 null일 수 없습니다") // Long id 제거

    return Address.reconstitute(
        // id = id, // Long id 제거
        addressId = AddressId.of(this.addressId), // addressId 매핑 추가
        street = street,
        detail = detail,
        zipCode = zipCode,
        isDefault = isDefault,
    )
}

// Address -> AddressEntity 변환 (UserEntity 참조 없이)
fun Address.toEntity(): AddressEntity =
    AddressEntity(
        id = null, // JPA가 Long ID를 관리하도록 null 전달 (기존 id 필드 사용 제거)
        addressId = this.addressId.value, // addressId 매핑 추가
        street = street,
        detail = detail,
        zipCode = zipCode,
        isDefault = isDefault,
        // version은 JPA가 관리하거나, 필요시 Domain 객체에서 전달받아 설정
    )
