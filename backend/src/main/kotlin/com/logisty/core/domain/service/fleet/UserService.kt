package com.logisty.core.domain.service.fleet

import com.logisty.core.domain.BusinessExceptions.UserNotFoundException
import com.logisty.core.domain.model.User
import com.logisty.core.domain.model.query.GetUsersQuery
import com.logisty.core.domain.model.values.UserId
import com.logisty.core.domain.model.values.UserRole
import com.logisty.core.domain.port.UserRepository
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Service
@Transactional
class UserService(
    private val userRepository: UserRepository,
) {
    fun getUsers(query: GetUsersQuery): Pair<List<User>, Long> = userRepository.findUsers(query)

    fun userHasRole(
        userId: UserId,
        vararg roles: UserRole,
    ): Boolean =
        (userRepository.findById(userId) ?: throw UserNotFoundException())
            .let { user -> user.roles.any { it in roles } }
}
