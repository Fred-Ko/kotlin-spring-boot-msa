package com.restaurant.user.application.query.usecase

import com.restaurant.user.application.query.dto.AddressDto
import com.restaurant.user.application.query.dto.GetUserAddressesQuery

/**
 * 사용자 주소 목록 조회 유스케이스 인터페이스
 */
interface GetUserAddressesQuery {
    fun getUserAddresses(query: com.restaurant.user.application.query.dto.GetUserAddressesQuery): List<AddressDto>
}
