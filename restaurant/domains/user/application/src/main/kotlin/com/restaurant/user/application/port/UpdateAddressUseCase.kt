package com.restaurant.user.application.port.input

import com.restaurant.user.application.dto.command.UpdateAddressCommand

/**
 * 주소 업데이트 유스케이스 인터페이스 (Rule App-Struct)
 */
interface UpdateAddressUseCase {
    fun updateAddress(command: UpdateAddressCommand)
}
