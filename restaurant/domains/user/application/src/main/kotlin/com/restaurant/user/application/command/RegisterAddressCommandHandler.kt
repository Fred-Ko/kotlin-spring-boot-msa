package com.restaurant.user.application.command

import com.restaurant.user.application.command.dto.RegisterAddressCommand
import com.restaurant.user.domain.vo.AddressId

/**
 * 주소 등록 커맨드 핸들러 인터페이스
 */
interface IRegisterAddressCommandHandler {
    fun registerAddress(command: RegisterAddressCommand): AddressId
}
