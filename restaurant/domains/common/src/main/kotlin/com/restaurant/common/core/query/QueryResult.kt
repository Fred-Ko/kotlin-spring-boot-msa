package com.restaurant.common.core.query

data class QueryResult<T>(
  val success: Boolean,
  val data: T? = null,
  val errorCode: String? = null,
)
