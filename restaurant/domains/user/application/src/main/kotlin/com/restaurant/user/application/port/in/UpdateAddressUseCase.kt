package com.restaurant.user.application.port.`in`

import com.restaurant.user.application.dto.command.UpdateAddressCommand // Command DTO 임포트 추가

/**
 * 주소 업데이트 유스케이스 인터페이스 (Rule App-Struct)
 */
interface UpdateAddressUseCase {
    fun updateAddress(command: UpdateAddressCommand)
}
