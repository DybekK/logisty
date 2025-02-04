package com.logisty.core.domain

import com.logisty.core.application.persistence.tables.Fleets
import com.logisty.core.application.persistence.tables.Invitations
import com.logisty.core.application.persistence.tables.Users
import com.logisty.core.domain.model.FixtureFleet
import com.logisty.core.domain.model.FixtureInvitation
import com.logisty.core.domain.model.FixtureUser
import com.logisty.core.domain.model.values.ApartmentNumber
import com.logisty.core.domain.model.values.City
import com.logisty.core.domain.model.values.FirstName
import com.logisty.core.domain.model.values.FleetId
import com.logisty.core.domain.model.values.FleetName
import com.logisty.core.domain.model.values.InvitationId
import com.logisty.core.domain.model.values.InvitationStatus
import com.logisty.core.domain.model.values.LastName
import com.logisty.core.domain.model.values.PhoneNumber
import com.logisty.core.domain.model.values.PostalCode
import com.logisty.core.domain.model.values.StateProvince
import com.logisty.core.domain.model.values.Street
import com.logisty.core.domain.model.values.StreetNumber
import com.logisty.core.domain.model.values.UserEmail
import com.logisty.core.domain.model.values.UserId
import com.logisty.core.domain.model.values.UserPassword
import com.logisty.core.domain.model.values.UserRole
import org.jetbrains.exposed.sql.insert
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder
import java.time.Duration
import java.time.Instant
import java.time.LocalDate
import java.util.UUID

class Fixtures {
    private val encoder = BCryptPasswordEncoder()

    val fleet =
        FixtureFleet(
            fleetId = FleetId.generate(),
            fleetName = FleetName("fleet-name"),
        )

    val user =
        FixtureUser(
            userId = UserId.generate(),
            fleetId = fleet.fleetId,
            firstName = FirstName("first-name"),
            lastName = LastName("last-name"),
            email = UserEmail("user@example.com"),
            password = UserPassword("password"),
            phoneNumber = PhoneNumber("123456789"),
            dateOfBirth = LocalDate.now().minusYears(18),
            street = Street("Main Street"),
            streetNumber = StreetNumber("123"),
            apartmentNumber = ApartmentNumber("A1"),
            city = City("New York"),
            stateProvince = StateProvince("NY"),
            postalCode = PostalCode("10001"),
            roles = listOf(UserRole.DISPATCHER),
        )

    val invitation =
        FixtureInvitation(
            invitationId = InvitationId.generate(),
            fleetId = fleet.fleetId,
            fleetName = fleet.fleetName,
            email = user.email,
            firstName = user.firstName,
            lastName = user.lastName,
            status = InvitationStatus.ACCEPTED,
            roles = listOf(UserRole.DRIVER),
            createdAt = Instant.now(),
            expiresAt = Instant.now().plus(Duration.ofDays(7)),
            phoneNumber = user.phoneNumber,
            dateOfBirth = user.dateOfBirth,
            street = user.street,
            streetNumber = user.streetNumber,
            apartmentNumber = user.apartmentNumber,
            city = user.city,
            stateProvince = user.stateProvince,
            postalCode = user.postalCode,
            acceptedAt = Instant.now(),
        )

    fun createFleet() =
        Fleets.insert {
            it[fleetId] = fleet.fleetId.value
            it[fleetName] = fleet.fleetName.value
        }

    fun createInvitation() =
        Invitations.insert {
            it[invitationId] = invitation.invitationId.value
            it[fleetId] = invitation.fleetId.value
            it[email] = invitation.email.value
            it[firstName] = invitation.firstName.value
            it[lastName] = invitation.lastName.value
            it[status] = invitation.status.name
            it[roles] = invitation.roles.map { it.name }
            it[phoneNumber] = invitation.phoneNumber.value
            it[dateOfBirth] = invitation.dateOfBirth
            it[street] = invitation.street.value
            it[streetNumber] = invitation.streetNumber.value
            it[apartmentNumber] = invitation.apartmentNumber.value
            it[city] = invitation.city.value
            it[stateProvince] = invitation.stateProvince.value
            it[postalCode] = invitation.postalCode.value
            it[createdAt] = invitation.createdAt
            it[expiresAt] = invitation.expiresAt
            it[acceptedAt] = invitation.acceptedAt
        }

    fun createUser() =
        Users.insert {
            it[userId] = user.userId.value
            it[fleetId] = user.fleetId.value
            it[email] = user.email.value
            it[firstName] = user.firstName.value
            it[lastName] = user.lastName.value
            it[password] = encoder.encode(user.password.value)
            it[roles] = user.roles.map { it.name }
            it[phoneNumber] = user.phoneNumber.value
            it[dateOfBirth] = user.dateOfBirth
            it[street] = user.street.value
            it[streetNumber] = user.streetNumber.value
            it[apartmentNumber] = user.apartmentNumber.value
            it[city] = user.city.value
            it[stateProvince] = user.stateProvince.value
            it[postalCode] = user.postalCode.value
        }
}

fun generateUserEmail() = UserEmail("user_${UUID.randomUUID()}@example.com")
