package com.restaurant.user.application.command.usecase

import com.restaurant.user.application.dto.command.UpdateProfileCommand

/**
 * 프로필 업데이트 유스케이스 인터페이스 (Rule App-Struct)
 */
interface UpdateProfileUseCase {
    fun updateProfile(command: UpdateProfileCommand)
}
