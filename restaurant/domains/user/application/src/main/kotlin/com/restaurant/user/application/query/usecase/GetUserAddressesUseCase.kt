package com.restaurant.user.application.query.usecase

import com.restaurant.user.application.query.dto.AddressDto
import com.restaurant.user.application.query.dto.GetUserAddressesQuery

/**
 * 특정 사용자의 모든 주소 조회 유스케이스 인터페이스
 */
interface GetUserAddressesUseCase {
    fun getUserAddresses(query: GetUserAddressesQuery): List<AddressDto>
}
