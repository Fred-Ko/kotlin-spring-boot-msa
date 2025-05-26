package com.restaurant.user.application.command.usecase

import com.restaurant.user.application.command.dto.UpdateAddressCommand

/**
 * 주소 업데이트 유스케이스 인터페이스 (Rule App-Struct)
 */
interface UpdateAddressUseCase {
    fun updateAddress(command: UpdateAddressCommand)
}
