package com.logisty.core.adapter.outbound

import com.logisty.core.application.persistence.tables.Fleets
import com.logisty.core.application.persistence.tables.Invitations
import com.logisty.core.domain.model.Invitation
import com.logisty.core.domain.model.command.CreateInvitationCommand
import com.logisty.core.domain.model.query.GetInvitationsQuery
import com.logisty.core.domain.model.values.ApartmentNumber
import com.logisty.core.domain.model.values.City
import com.logisty.core.domain.model.values.FirstName
import com.logisty.core.domain.model.values.FleetId
import com.logisty.core.domain.model.values.FleetName
import com.logisty.core.domain.model.values.InvitationId
import com.logisty.core.domain.model.values.InvitationStatus
import com.logisty.core.domain.model.values.InvitationStatus.ACCEPTED
import com.logisty.core.domain.model.values.InvitationStatus.EXPIRED
import com.logisty.core.domain.model.values.InvitationStatus.PENDING
import com.logisty.core.domain.model.values.LastName
import com.logisty.core.domain.model.values.PhoneNumber
import com.logisty.core.domain.model.values.PostalCode
import com.logisty.core.domain.model.values.StateProvince
import com.logisty.core.domain.model.values.Street
import com.logisty.core.domain.model.values.StreetNumber
import com.logisty.core.domain.model.values.UserEmail
import com.logisty.core.domain.model.values.UserRole
import com.logisty.core.domain.port.InvitationRepository
import org.jetbrains.exposed.sql.Op
import org.jetbrains.exposed.sql.ResultRow
import org.jetbrains.exposed.sql.SortOrder
import org.jetbrains.exposed.sql.andWhere
import org.jetbrains.exposed.sql.insert
import org.jetbrains.exposed.sql.selectAll
import org.jetbrains.exposed.sql.update
import org.springframework.stereotype.Repository
import java.time.Instant
import java.time.temporal.ChronoUnit

@Repository
class PostgresInvitationRepository : InvitationRepository {
    override fun findInvitations(query: GetInvitationsQuery): Pair<List<Invitation>, Long> {
        val total =
            Invitations
                .selectAll()
                .where { Invitations.fleetId eq query.fleetId.value }
                .andWhere { query.status?.let { Invitations.status eq it.name } ?: Op.TRUE }
                .andWhere { query.email?.let { Invitations.email like "%${it.value}%" } ?: Op.TRUE }
                .count()

        val invitations =
            (Invitations innerJoin Fleets)
                .selectAll()
                .where { Invitations.fleetId eq query.fleetId.value }
                .andWhere { query.status?.let { Invitations.status eq it.name } ?: Op.TRUE }
                .andWhere { query.email?.let { Invitations.email like "%${it.value}%" } ?: Op.TRUE }
                .orderBy(Invitations.createdAt to SortOrder.DESC)
                .limit(query.limit)
                .offset((query.page * query.limit).toLong())
                .map { it.toInvitation() }

        return Pair(invitations, total)
    }

    override fun findInvitationsByStatus(status: InvitationStatus): List<Invitation> =
        (Invitations innerJoin Fleets)
            .selectAll()
            .where { Invitations.status eq status.name }
            .map { it.toInvitation() }

    override fun findInvitationById(id: InvitationId): Invitation? =
        (Invitations innerJoin Fleets)
            .selectAll()
            .where { Invitations.invitationId eq id.value }
            .singleOrNull()
            ?.toInvitation()

    override fun findInvitationByEmail(email: UserEmail): Invitation? =
        (Invitations innerJoin Fleets)
            .selectAll()
            .where { Invitations.email eq email.value }
            .singleOrNull()
            ?.toInvitation()

    override fun createInvitation(
        command: CreateInvitationCommand,
        createdAt: Instant,
    ): InvitationId {
        val invitationId = InvitationId.generate()
        val expiresAt = createdAt.plus(7, ChronoUnit.DAYS)

        Invitations.insert {
            it[Invitations.invitationId] = invitationId.value
            it[Invitations.fleetId] = command.fleetId.value
            it[Invitations.email] = command.email.value
            it[Invitations.firstName] = command.firstName.value
            it[Invitations.lastName] = command.lastName.value
            it[Invitations.phoneNumber] = command.phoneNumber.value
            it[Invitations.dateOfBirth] = command.dateOfBirth
            it[Invitations.street] = command.street.value
            it[Invitations.streetNumber] = command.streetNumber.value
            it[Invitations.apartmentNumber] = command.apartmentNumber?.value
            it[Invitations.city] = command.city.value
            it[Invitations.stateProvince] = command.stateProvince.value
            it[Invitations.postalCode] = command.postalCode.value
            it[Invitations.status] = PENDING.name
            it[Invitations.roles] = command.roles.map { it.name }
            it[Invitations.createdAt] = createdAt
            it[Invitations.expiresAt] = expiresAt
            it[Invitations.acceptedAt] = null
        }

        return invitationId
    }

    override fun acceptInvitation(invitationId: InvitationId): InvitationId {
        Invitations.update({ Invitations.invitationId eq invitationId.value }) {
            it[Invitations.status] = ACCEPTED.name
            it[Invitations.acceptedAt] = Instant.now()
        }

        return invitationId
    }

    override fun expireInvitation(invitationId: InvitationId): InvitationId {
        Invitations.update({ Invitations.invitationId eq invitationId.value }) {
            it[Invitations.status] = EXPIRED.name
        }

        return invitationId
    }
}

private fun ResultRow.toInvitation(): Invitation =
    Invitation(
        invitationId = InvitationId(this[Invitations.invitationId]),
        fleetId = FleetId(this[Invitations.fleetId]),
        fleetName = FleetName(this[Fleets.fleetName]),
        email = UserEmail(this[Invitations.email]),
        firstName = FirstName(this[Invitations.firstName]),
        lastName = LastName(this[Invitations.lastName]),
        phoneNumber = PhoneNumber(this[Invitations.phoneNumber]),
        dateOfBirth = this[Invitations.dateOfBirth],
        street = Street(this[Invitations.street]),
        streetNumber = StreetNumber(this[Invitations.streetNumber]),
        apartmentNumber = this[Invitations.apartmentNumber]?.let { ApartmentNumber(it) },
        city = City(this[Invitations.city]),
        stateProvince = StateProvince(this[Invitations.stateProvince]),
        postalCode = PostalCode(this[Invitations.postalCode]),
        status = InvitationStatus.valueOf(this[Invitations.status]),
        roles = this[Invitations.roles].map { UserRole.valueOf(it) },
        createdAt = this[Invitations.createdAt],
        expiresAt = this[Invitations.expiresAt],
        acceptedAt = this[Invitations.acceptedAt],
    )
