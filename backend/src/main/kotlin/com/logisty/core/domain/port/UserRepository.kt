package com.logisty.core.domain.port

import com.logisty.core.domain.model.User
import com.logisty.core.domain.model.command.CreateUserCommand
import com.logisty.core.domain.model.query.GetUsersQuery
import com.logisty.core.domain.model.values.UserEmail
import com.logisty.core.domain.model.values.UserId

interface UserRepository {
    fun createUser(command: CreateUserCommand): UserId

    fun findUsers(query: GetUsersQuery): Pair<List<User>, Long>

    fun findUsers(userIds: List<UserId>): List<User>

    fun findById(userId: UserId): User?

    fun findByEmail(email: UserEmail): User?

    fun findUserById(id: UserId): User?
}
