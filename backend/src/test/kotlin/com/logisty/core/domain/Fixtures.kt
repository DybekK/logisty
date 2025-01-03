package com.logisty.core.domain

import com.logisty.core.application.persistence.tables.Fleets
import com.logisty.core.application.persistence.tables.Invitations
import com.logisty.core.application.persistence.tables.Users
import com.logisty.core.domain.model.FixtureFleet
import com.logisty.core.domain.model.FixtureInvitation
import com.logisty.core.domain.model.FixtureUser
import com.logisty.core.domain.model.values.FirstName
import com.logisty.core.domain.model.values.FleetId
import com.logisty.core.domain.model.values.FleetName
import com.logisty.core.domain.model.values.InvitationId
import com.logisty.core.domain.model.values.InvitationStatus
import com.logisty.core.domain.model.values.LastName
import com.logisty.core.domain.model.values.UserEmail
import com.logisty.core.domain.model.values.UserId
import com.logisty.core.domain.model.values.UserPassword
import org.jetbrains.exposed.sql.insert
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder
import java.time.Duration
import java.time.Instant
import java.util.UUID

class Fixtures {
    private val encoder = BCryptPasswordEncoder()

    val fleet =
        FixtureFleet(
            fleetId = FleetId(UUID.randomUUID()),
            fleetName = FleetName("fleet-name"),
        )

    val user =
        FixtureUser(
            userId = UserId(UUID.randomUUID()),
            firstName = FirstName("first-name"),
            lastName = LastName("last-name"),
            email = UserEmail("user@example.com"),
            password = UserPassword("password"),
        )

    val invitation =
        FixtureInvitation(
            invitationId = InvitationId(UUID.randomUUID()),
            fleetId = fleet.fleetId,
            email = user.email,
            firstName = user.firstName,
            lastName = user.lastName,
            status = InvitationStatus.ACCEPTED,
            createdAt = Instant.now(),
            expiresAt = Instant.now().plus(Duration.ofDays(7)),
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
            it[createdAt] = invitation.createdAt
            it[expiresAt] = invitation.expiresAt
            it[acceptedAt] = invitation.acceptedAt
        }

    fun createUser() =
        Users.insert {
            it[userId] = user.userId.value
            it[fleetId] = fleet.fleetId.value
            it[email] = user.email.value
            it[firstName] = user.firstName.value
            it[lastName] = user.lastName.value
            it[password] = encoder.encode(user.password.value)
        }
}
