package com.restaurant.user.application.command.usecase

import com.restaurant.user.application.command.dto.RegisterAddressCommand
import com.restaurant.user.domain.vo.AddressId

/**
 * 주소 등록 유스케이스 인터페이스
 */
interface RegisterAddressUseCase {
    fun registerAddress(command: RegisterAddressCommand): AddressId
}
