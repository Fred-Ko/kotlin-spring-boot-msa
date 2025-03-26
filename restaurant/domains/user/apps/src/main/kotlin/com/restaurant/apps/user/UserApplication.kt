package com.restaurant.apps.user

import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.autoconfigure.domain.EntityScan
import org.springframework.boot.runApplication
import org.springframework.context.annotation.ComponentScan
import org.springframework.data.jpa.repository.config.EnableJpaRepositories

@SpringBootApplication
@ComponentScan(basePackages = ["com.restaurant"])
@EntityScan(basePackages = ["com.restaurant.infrastructure"])
@EnableJpaRepositories(basePackages = ["com.restaurant.infrastructure"])
class UserApplication

fun main(args: Array<String>) {
  runApplication<UserApplication>(*args)
}
