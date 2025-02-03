package com.ddd.support.exception

abstract class ApplicationException(message: String, cause: Throwable? = null) : RuntimeException(message, cause)