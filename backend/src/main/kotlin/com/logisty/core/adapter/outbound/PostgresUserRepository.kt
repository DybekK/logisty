package com.logisty.core.adapter.outbound

import com.logisty.core.application.persistence.tables.Users
import com.logisty.core.domain.model.User
import com.logisty.core.domain.model.command.CreateUserCommand
import com.logisty.core.domain.model.values.ApartmentNumber
import com.logisty.core.domain.model.values.City
import com.logisty.core.domain.model.values.FirstName
import com.logisty.core.domain.model.values.FleetId
import com.logisty.core.domain.model.values.LastName
import com.logisty.core.domain.model.values.PhoneNumber
import com.logisty.core.domain.model.values.PostalCode
import com.logisty.core.domain.model.values.StateProvince
import com.logisty.core.domain.model.values.Street
import com.logisty.core.domain.model.values.StreetNumber
import com.logisty.core.domain.model.values.UserEmail
import com.logisty.core.domain.model.values.UserEncodedPassword
import com.logisty.core.domain.model.values.UserId
import com.logisty.core.domain.model.values.UserRole
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
    override fun createUser(command: CreateUserCommand): UserId {
        val userId = UserId.generate()

        Users.insert {
            it[Users.userId] = userId.value
            it[Users.fleetId] = command.fleetId.value
            it[Users.email] = command.email.value
            it[Users.firstName] = command.firstName.value
            it[Users.lastName] = command.lastName.value
            it[Users.password] = encoder.encode(command.password.value)
            it[Users.roles] = command.roles.map { it.name }
            it[Users.phoneNumber] = command.phoneNumber.value
            it[Users.dateOfBirth] = command.dateOfBirth
            it[Users.street] = command.street.value
            it[Users.streetNumber] = command.streetNumber.value
            it[Users.apartmentNumber] = command.apartmentNumber?.value
            it[Users.city] = command.city.value
            it[Users.stateProvince] = command.stateProvince.value
            it[Users.postalCode] = command.postalCode.value
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
        fleetId = FleetId(this[Users.fleetId]),
        email = UserEmail(this[Users.email]),
        firstName = FirstName(this[Users.firstName]),
        lastName = LastName(this[Users.lastName]),
        password = UserEncodedPassword(this[Users.password]),
        roles = this[Users.roles].map { UserRole.valueOf(it) },
        phoneNumber = PhoneNumber(this[Users.phoneNumber]),
        dateOfBirth = this[Users.dateOfBirth],
        street = Street(this[Users.street]),
        streetNumber = StreetNumber(this[Users.streetNumber]),
        apartmentNumber = this[Users.apartmentNumber]?.let { ApartmentNumber(it) },
        city = City(this[Users.city]),
        stateProvince = StateProvince(this[Users.stateProvince]),
        postalCode = PostalCode(this[Users.postalCode]),
    )
