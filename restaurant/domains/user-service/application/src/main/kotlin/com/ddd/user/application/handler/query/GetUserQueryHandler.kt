package com.ddd.user.application.handler.query

import com.ddd.user.application.dto.query.GetUserQueryResult
import com.ddd.user.application.exception.UserApplicationException
import com.ddd.user.application.query.GetUserQuery
import com.ddd.user.domain.port.repository.UserRepository
import java.util.UUID
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Service
@Transactional(readOnly = true)
class GetUserQueryHandler(private val userRepository: UserRepository) : GetUserQuery {
    override fun getUser(id: String): GetUserQueryResult {
        val userId =
                try {
                    UUID.fromString(id)
                } catch (e: IllegalArgumentException) {
                    throw UserApplicationException.UserNotFound(id, e)
                }
        val user =
                userRepository.findById(userId) ?: throw UserApplicationException.UserNotFound(id)
        return GetUserQueryResult(
                id = user.id.toString(),
                email = user.email.value,
                name = user.name.value,
                phoneNumber = user.phoneNumber.value,
                street = user.address.street,
                city = user.address.city,
                state = user.address.state,
                zipCode = user.address.zipCode,
                active = user.isActivate()
        )
    }
}
