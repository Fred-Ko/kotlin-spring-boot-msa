package com.ddd.restaurant.application.query.usecase

import com.ddd.restaurant.application.query.query.GetRestaurantsQuery
import com.ddd.restaurant.application.query.result.GetRestaurantsResult
import com.ddd.support.application.usecase.QueryUseCase

interface GetRestaurantsUseCase : QueryUseCase<GetRestaurantsQuery, GetRestaurantsResult>
