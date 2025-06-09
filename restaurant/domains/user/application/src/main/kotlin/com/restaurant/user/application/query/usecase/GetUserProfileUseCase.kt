package com.restaurant.user.application.query.usecase

import com.restaurant.user.application.query.dto.GetUserProfileByIdQuery
import com.restaurant.user.application.query.dto.UserProfileDto

/**
 * 사용자 프로필 조회 유스케이스 인터페이스
 */
interface GetUserProfileUseCase {
    fun getUserProfile(query: GetUserProfileByIdQuery): UserProfileDto
}
