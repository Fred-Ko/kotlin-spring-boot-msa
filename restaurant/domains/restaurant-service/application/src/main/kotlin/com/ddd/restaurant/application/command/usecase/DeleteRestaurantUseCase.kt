package com.ddd.restaurant.application.command.usecase

import com.ddd.restaurant.application.command.command.DeleteRestaurantCommand
import com.ddd.restaurant.application.command.result.DeleteRestaurantResult
import com.ddd.support.application.usecase.CommandUseCase

interface DeleteRestaurantUseCase : CommandUseCase<DeleteRestaurantCommand, DeleteRestaurantResult>
