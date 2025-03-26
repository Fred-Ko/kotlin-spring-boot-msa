package com.restaurant.application.user.exception

import com.restaurant.common.core.exception.ApplicationException

open class UserApplicationException(
  message: String,
) : ApplicationException(message)

open class UserRegistrationException(
  message: String,
) : UserApplicationException(message)

open class UserAuthenticationException(
  message: String,
) : UserApplicationException(message)

open class UserProfileUpdateException(
  message: String,
) : UserApplicationException(message)

open class UserPasswordChangeException(
  message: String,
) : UserApplicationException(message)

open class UserDeletionException(
  message: String,
) : UserApplicationException(message)

open class UserQueryException(
  message: String,
) : UserApplicationException(message)

class UserNotFoundApplicationException(
  message: String,
) : UserQueryException(message)
