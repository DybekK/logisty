package com.logisty.core.domain.service.fleet

import com.logisty.core.domain.model.User
import com.logisty.core.domain.model.query.GetUsersQuery
import com.logisty.core.domain.port.UserRepository
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Service
@Transactional
class UserService(
    private val userRepository: UserRepository,
) {
    fun getUsers(query: GetUsersQuery): Pair<List<User>, Long> = userRepository.findUsers(query)
}
