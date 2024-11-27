package com.logisty.core.domain.port

import com.logisty.core.domain.model.User
import com.logisty.core.domain.model.values.FleetId
import com.logisty.core.domain.model.values.UserEmail
import com.logisty.core.domain.model.values.UserId
import com.logisty.core.domain.model.values.FirstName
import com.logisty.core.domain.model.values.LastName
import com.logisty.core.domain.model.values.UserPassword

interface UserRepository {
    fun createUser(
        fleetId: FleetId,
        firstName: FirstName,
        lastName: LastName,
        email: UserEmail,
        password: UserPassword,
    ): UserId

    fun findById(userId: UserId): User?

    fun findByEmail(email: UserEmail): User?
}
