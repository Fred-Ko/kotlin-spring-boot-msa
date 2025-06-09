package com.restaurant.user.application.command.usecase

import com.restaurant.user.application.command.dto.DeleteAddressCommand

/**
 * 주소 삭제 유스케이스 인터페이스
 */
interface DeleteAddressUseCase {
    fun deleteAddress(command: DeleteAddressCommand)
}
