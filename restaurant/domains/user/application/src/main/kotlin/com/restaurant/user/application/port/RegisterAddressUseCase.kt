package com.restaurant.user.application.port

import com.restaurant.user.application.dto.command.RegisterAddressCommand
import com.restaurant.user.domain.vo.AddressId

/**
 * 주소 등록 유스케이스 인터페이스 (Rule App-Struct)
 */
interface RegisterAddressUseCase {
    fun registerAddress(command: RegisterAddressCommand): AddressId
}
