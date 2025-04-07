package com.restaurant.domain.user.exception

/**
 * 주소를 찾을 수 없을 때 발생하는 예외
 */
class AddressNotFoundException(
    val userId: Long,
    val addressId: Long,
) : UserDomainException("사용자($userId)의 주소($addressId)를 찾을 수 없습니다.")
