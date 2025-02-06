package com.ddd.user.application.query.usecase

import com.ddd.user.application.query.dto.query.GetUserQuery
import com.ddd.support.application.usecase.QueryUseCase
import com.ddd.user.application.query.dto.UserDto

interface GetUserUseCase : QueryUseCase<GetUserQuery, UserDto>
