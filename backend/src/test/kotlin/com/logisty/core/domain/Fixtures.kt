package com.logisty.core.domain

import com.logisty.core.application.persistence.tables.Fleets
import com.logisty.core.application.persistence.tables.Invitations
import com.logisty.core.application.persistence.tables.Users
import com.logisty.core.domain.model.FixtureFleet
import com.logisty.core.domain.model.FixtureInvitation
import com.logisty.core.domain.model.FixtureOrder
import com.logisty.core.domain.model.FixtureOrderRoute
import com.logisty.core.domain.model.FixtureOrderStep
import com.logisty.core.domain.model.FixtureUser
import com.logisty.core.domain.model.values.ApartmentNumber
import com.logisty.core.domain.model.values.City
import com.logisty.core.domain.model.values.FirstName
import com.logisty.core.domain.model.values.FleetId
import com.logisty.core.domain.model.values.FleetName
import com.logisty.core.domain.model.values.InvitationId
import com.logisty.core.domain.model.values.InvitationStatus
import com.logisty.core.domain.model.values.LastName
import com.logisty.core.domain.model.values.OrderId
import com.logisty.core.domain.model.values.OrderRouteId
import com.logisty.core.domain.model.values.OrderStepId
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
import org.postgis.LineString
import org.postgis.Point
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

    private val users =
        listOf(
            FixtureUser(
                userId = UserId.generate(),
                fleetId = fleet.fleetId,
                firstName = FirstName("first-name"),
                lastName = LastName("last-name"),
                email = UserEmail("dispatcher@example.com"),
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
                createdAt = Instant.now(),
            ),
            FixtureUser(
                userId = UserId.generate(),
                fleetId = fleet.fleetId,
                firstName = FirstName("first-name"),
                lastName = LastName("last-name"),
                email = UserEmail("driver@example.com"),
                password = UserPassword("password"),
                phoneNumber = PhoneNumber("123456789"),
                dateOfBirth = LocalDate.now().minusYears(18),
                street = Street("Main Street"),
                streetNumber = StreetNumber("123"),
                apartmentNumber = ApartmentNumber("A1"),
                city = City("New York"),
                stateProvince = StateProvince("NY"),
                postalCode = PostalCode("10001"),
                roles = listOf(UserRole.DRIVER),
                createdAt = Instant.now(),
            ),
        )

    private val invitations =
        users.map { user ->
            FixtureInvitation(
                invitationId = InvitationId.generate(),
                fleetId = fleet.fleetId,
                fleetName = fleet.fleetName,
                email = user.email,
                firstName = user.firstName,
                lastName = user.lastName,
                status = InvitationStatus.ACCEPTED,
                roles = user.roles,
                createdAt = user.createdAt,
                expiresAt = user.createdAt.plus(Duration.ofDays(7)),
                phoneNumber = user.phoneNumber,
                dateOfBirth = user.dateOfBirth,
                street = user.street,
                streetNumber = user.streetNumber,
                apartmentNumber = user.apartmentNumber,
                city = user.city,
                stateProvince = user.stateProvince,
                postalCode = user.postalCode,
                acceptedAt = user.createdAt.plus(Duration.ofDays(7)),
            )
        }

    val dispatcher = users.first()
    val driver = users.last()
    val invitation = invitations.first()
    val driverInvitation = invitations.last()

    val order =
        {
            val orderId = OrderId.generate()
            val orderRouteId = OrderRouteId.generate()
            val orderStepId = OrderStepId.generate()

            val startedAt = Instant.now()
            val endedAt = startedAt.plus(Duration.ofMinutes(20))

            FixtureOrder(
                orderId = orderId,
                fleetId = fleet.fleetId,
                driverId = driver.userId,
                steps =
                    listOf(
                        FixtureOrderStep(
                            orderStepId = orderStepId,
                            description = "step-1",
                            location = Point(1.0, 2.0),
                        ),
                        FixtureOrderStep(
                            orderStepId = orderStepId,
                            description = "step-2",
                            location = Point(3.0, 4.0),
                            estimatedArrivalAt = endedAt,
                        ),
                    ),
                route =
                    FixtureOrderRoute(
                        orderRouteId = orderRouteId,
                        orderId = orderId,
                        route = LineString(arrayOf(Point(1.0, 2.0), Point(3.0, 4.0))),
                        duration = 100.0,
                        distance = 100.0,
                    ),
                createdBy = dispatcher.userId,
                createdAt = Instant.now(),
                estimatedStartedAt = startedAt,
                estimatedEndedAt = endedAt,
            )
        }()

    fun createFleet() =
        Fleets.insert {
            it[fleetId] = fleet.fleetId.value
            it[fleetName] = fleet.fleetName.value
        }

    fun createInvitations() =
        invitations.forEach { invitation ->
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
        }

    fun createUsers() =
        users.forEach { user ->
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
                it[createdAt] = user.createdAt
            }
        }
}

fun generateUserEmail() = UserEmail("user_${UUID.randomUUID()}@example.com")
