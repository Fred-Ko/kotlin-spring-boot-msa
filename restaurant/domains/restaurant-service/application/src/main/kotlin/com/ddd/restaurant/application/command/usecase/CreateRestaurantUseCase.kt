package com.ddd.restaurant.application.command.usecase

import com.ddd.restaurant.application.command.command.CreateRestaurantCommand
import com.ddd.restaurant.application.command.result.CreateRestaurantResult
import com.ddd.support.application.usecase.CommandUseCase

interface CreateRestaurantUseCase : CommandUseCase<CreateRestaurantCommand, CreateRestaurantResult>
