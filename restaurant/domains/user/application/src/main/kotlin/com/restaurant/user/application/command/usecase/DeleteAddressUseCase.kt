package com.restaurant.user.application.command.usecase

import com.restaurant.user.application.dto.command.DeleteAddressCommand

/**
 * 주소 삭제 유스케이스 인터페이스 (Rule App-Struct)
 */
interface DeleteAddressUseCase {
    fun deleteAddress(command: DeleteAddressCommand)
}
