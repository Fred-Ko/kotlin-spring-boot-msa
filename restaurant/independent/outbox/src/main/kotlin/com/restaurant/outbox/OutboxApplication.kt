package com.restaurant.outbox

import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.runApplication

@SpringBootApplication
class OutboxApplication

fun main(args: Array<String>) {
    runApplication<OutboxApplication>(*args)
}
