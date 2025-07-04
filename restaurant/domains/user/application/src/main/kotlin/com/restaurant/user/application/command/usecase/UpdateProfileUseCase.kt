package com.restaurant.user.application.command.usecase

import com.restaurant.user.application.command.dto.UpdateProfileCommand

/**
 * 프로필 업데이트 유스케이스 인터페이스
 */
interface UpdateProfileUseCase {
    fun updateProfile(command: UpdateProfileCommand)
}
