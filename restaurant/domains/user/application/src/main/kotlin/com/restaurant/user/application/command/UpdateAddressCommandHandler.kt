package com.restaurant.user.application.command

import com.restaurant.user.application.command.dto.UpdateAddressCommand

/**
 * 주소 업데이트 커맨드 핸들러 인터페이스
 */
interface IUpdateAddressCommandHandler {
    fun updateAddress(command: UpdateAddressCommand)
}
