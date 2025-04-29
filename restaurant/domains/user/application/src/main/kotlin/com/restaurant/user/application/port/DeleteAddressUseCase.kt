package com.restaurant.user.application.port.input

import com.restaurant.user.application.dto.command.DeleteAddressCommand // Command DTO 임포트 추가

/**
 * 주소 삭제 유스케이스 인터페이스 (Rule App-Struct)
 */
interface DeleteAddressUseCase {
    fun deleteAddress(command: DeleteAddressCommand)
}
