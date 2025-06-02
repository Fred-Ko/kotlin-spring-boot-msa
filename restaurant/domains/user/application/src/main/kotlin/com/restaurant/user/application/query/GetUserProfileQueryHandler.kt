package com.restaurant.user.application.query

import com.restaurant.user.application.query.dto.GetUserProfileByIdQuery
import com.restaurant.user.application.query.dto.UserProfileDto

/**
 * 사용자 프로필 조회 쿼리 핸들러 인터페이스
 */
interface GetUserProfileQueryHandler {
    fun getUserProfile(query: GetUserProfileByIdQuery): UserProfileDto
}
