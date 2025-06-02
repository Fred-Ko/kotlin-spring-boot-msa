package com.restaurant.user.application.command

import com.restaurant.user.application.command.dto.UpdateProfileCommand

/**
 * 프로필 업데이트 커맨드 핸들러 인터페이스
 */
interface IUpdateProfileCommandHandler {
    fun updateProfile(command: UpdateProfileCommand)
}
