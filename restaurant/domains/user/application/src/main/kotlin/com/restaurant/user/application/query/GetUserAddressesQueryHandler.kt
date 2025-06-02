package com.restaurant.user.application.query

import com.restaurant.user.application.query.dto.AddressDto
import com.restaurant.user.application.query.dto.GetUserAddressesQuery

/**
 * 사용자 주소 목록 조회 쿼리 핸들러 인터페이스
 */
interface GetUserAddressesQueryHandler {
    fun getUserAddresses(query: com.restaurant.user.application.query.dto.GetUserAddressesQuery): List<AddressDto>
}
