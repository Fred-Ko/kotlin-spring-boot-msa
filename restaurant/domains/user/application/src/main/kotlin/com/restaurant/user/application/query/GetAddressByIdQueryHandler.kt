package com.restaurant.user.application.query

import com.restaurant.user.application.query.dto.AddressDto
import com.restaurant.user.application.query.dto.GetAddressByIdQuery

/**
 * 특정 주소 조회 쿼리 핸들러 인터페이스
 */
interface GetAddressByIdQueryHandler {
    fun getAddressById(query: com.restaurant.user.application.query.dto.GetAddressByIdQuery): AddressDto
}
