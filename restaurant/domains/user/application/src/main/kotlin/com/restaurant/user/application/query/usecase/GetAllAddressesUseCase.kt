package com.restaurant.user.application.query.usecase

import com.restaurant.user.application.query.dto.AddressDto
import com.restaurant.user.application.query.dto.GetUserAddressesQuery

/**
 * 모든 주소 조회 유스케이스 인터페이스
 */
interface GetAllAddressesUseCase {
    fun getAllAddresses(query: GetUserAddressesQuery): List<AddressDto>
}
