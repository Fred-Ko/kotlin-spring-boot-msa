package com.ddd.support.application.usecase

interface CommandUseCase<in Command, out Result> {
    fun execute(command: Command): Result
}

interface QueryUseCase<in Query, out Result> {
    fun execute(query: Query): Result
}
