package com.ddd.restaurant.application.query.usecase

import com.ddd.restaurant.application.query.query.GetRestaurantQuery
import com.ddd.restaurant.application.query.result.GetRestaurantResult
import com.ddd.support.application.usecase.QueryUseCase

interface GetRestaurantUseCase : QueryUseCase<GetRestaurantQuery, GetRestaurantResult>
