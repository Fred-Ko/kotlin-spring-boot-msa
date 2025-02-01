package com.ddd.restaurant.application.command.usecase

import com.ddd.restaurant.application.command.command.UpdateRestaurantCommand
import com.ddd.restaurant.application.command.result.UpdateRestaurantResult
import com.ddd.support.application.usecase.CommandUseCase

interface UpdateRestaurantUseCase : CommandUseCase<UpdateRestaurantCommand, UpdateRestaurantResult>
