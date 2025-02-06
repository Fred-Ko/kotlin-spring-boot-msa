package com.ddd.user.application.query.usecase

import com.ddd.support.application.usecase.QueryUseCase
import com.ddd.user.application.query.dto.UserDto
import com.ddd.user.application.query.dto.query.GetUsersQuery
import org.springframework.data.domain.Page

interface GetUsersUseCase : QueryUseCase<GetUsersQuery, Page<UserDto>>
