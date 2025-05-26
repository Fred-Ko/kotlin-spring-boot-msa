package com.restaurant.user.application.query.usecase

import com.restaurant.user.application.query.dto.AddressDto
import com.restaurant.user.application.query.dto.GetAddressByIdQuery

/**
 * 특정 주소 조회 유스케이스 인터페이스
 */
interface GetAddressByIdQuery {
    fun getAddressById(query: com.restaurant.user.application.query.dto.GetAddressByIdQuery): AddressDto
}
