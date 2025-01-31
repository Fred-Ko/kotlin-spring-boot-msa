package com.ddd.user.application.query.usecase

import com.ddd.user.application.query.query.GetUserQuery
import com.ddd.user.application.query.result.GetUserResult
import com.ddd.support.application.usecase.QueryUseCase

interface GetUserUseCase : QueryUseCase<GetUserQuery, GetUserResult>
