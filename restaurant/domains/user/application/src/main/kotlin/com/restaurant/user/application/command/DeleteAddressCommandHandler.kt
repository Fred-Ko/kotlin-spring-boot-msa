package com.restaurant.user.application.command

import com.restaurant.user.application.command.dto.DeleteAddressCommand

/**
 * 주소 삭제 커맨드 핸들러 인터페이스
 */
interface IDeleteAddressCommandHandler {
    fun deleteAddress(command: DeleteAddressCommand)
}
