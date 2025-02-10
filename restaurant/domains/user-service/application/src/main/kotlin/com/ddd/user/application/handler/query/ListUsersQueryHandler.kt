package com.ddd.user.application.handler.query

import com.ddd.user.application.dto.result.ListUsersQueryResult
import com.ddd.user.application.query.ListUsersQuery
import com.ddd.user.domain.repository.UserRepository
import org.springframework.data.domain.PageRequest
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Service
@Transactional(readOnly = true)
class ListUsersQueryHandler(private val userRepository: UserRepository) : ListUsersQuery {
    override fun listUsers(page: Int, size: Int): ListUsersQueryResult {
        val pageRequest = PageRequest.of(page, size)
        val userPage = userRepository.findAll(pageRequest)
        return ListUsersQueryResult(
                users =
                        userPage.content.map {
                            ListUsersQueryResult.User(
                                    id = it.id,
                                    email = it.email.value,
                                    name = it.name.value,
                                    active = it.isActivate()
                            )
                        },
                totalCount = userPage.totalElements,
                totalPages = userPage.totalPages
        )
    }
}
