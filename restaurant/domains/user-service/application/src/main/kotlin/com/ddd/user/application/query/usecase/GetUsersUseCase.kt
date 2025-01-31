package com.ddd.user.application.query.usecase

import com.ddd.support.application.usecase.QueryUseCase
import com.ddd.user.application.query.query.GetUsersQuery
import com.ddd.user.application.query.result.GetUsersResult

interface GetUsersUseCase : QueryUseCase<GetUsersQuery, GetUsersResult>
