package com.restaurant.presentation.user.extensions.v1.request

import com.restaurant.application.user.query.GetUserProfileQuery

// String (UUID) -> GetUserProfileQuery 변환
fun String.toGetUserProfileQuery(): GetUserProfileQuery =
    GetUserProfileQuery(
        userId = this,
    )
