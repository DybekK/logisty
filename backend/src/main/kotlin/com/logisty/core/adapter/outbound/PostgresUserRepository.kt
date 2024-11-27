package com.logisty.core.adapter.outbound

import com.logisty.core.application.persistence.tables.Users
import com.logisty.core.domain.model.User
import com.logisty.core.domain.model.values.FirstName
import com.logisty.core.domain.model.values.FleetId
import com.logisty.core.domain.model.values.LastName
import com.logisty.core.domain.model.values.UserEmail
import com.logisty.core.domain.model.values.UserEncodedPassword
import com.logisty.core.domain.model.values.UserId
import com.logisty.core.domain.model.values.UserPassword
import com.logisty.core.domain.port.UserRepository
import org.jetbrains.exposed.sql.ResultRow
import org.jetbrains.exposed.sql.insert
import org.jetbrains.exposed.sql.selectAll
import org.springframework.security.crypto.password.PasswordEncoder
import org.springframework.stereotype.Repository

@Repository
class PostgresUserRepository(
    private val encoder: PasswordEncoder,
) : UserRepository {
    override fun createUser(
        fleetId: FleetId,
        firstName: FirstName,
        lastName: LastName,
        email: UserEmail,
        password: UserPassword,
    ): UserId {
        val userId = UserId.generate()

        Users.insert {
            it[Users.userId] = userId.value
            it[Users.fleetId] = fleetId.value
            it[Users.email] = email.value
            it[Users.firstName] = firstName.value
            it[Users.lastName] = lastName.value
            it[Users.password] = encoder.encode(password.value)
        }

        return userId
    }

    override fun findById(userId: UserId): User? =
        Users
            .selectAll()
            .where { Users.userId eq userId.value }
            .singleOrNull()
            ?.toUser()

    override fun findByEmail(email: UserEmail): User? =
        Users
            .selectAll()
            .where { Users.email eq email.value }
            .singleOrNull()
            ?.toUser()
}

private fun ResultRow.toUser(): User =
    User(
        userId = UserId(this[Users.userId]),
        email = UserEmail(this[Users.email]),
        firstName = FirstName(this[Users.firstName]),
        lastName = LastName(this[Users.lastName]),
        password = UserEncodedPassword(this[Users.password]),
    )
