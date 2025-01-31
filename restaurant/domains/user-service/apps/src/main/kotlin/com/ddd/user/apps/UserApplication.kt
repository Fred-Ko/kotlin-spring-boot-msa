package com.ddd.user.apps

import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.autoconfigure.domain.EntityScan
import org.springframework.boot.runApplication
import org.springframework.context.annotation.ComponentScan
import org.springframework.data.jpa.repository.config.EnableJpaRepositories


@SpringBootApplication
@ComponentScan(basePackages = ["com.ddd.user"])
@EntityScan(basePackages = ["com.ddd.user"])
@EnableJpaRepositories(basePackages = ["com.ddd.user"])
class UserApplication

fun main(args: Array<String>) {
  runApplication<UserApplication>(*args)
}
