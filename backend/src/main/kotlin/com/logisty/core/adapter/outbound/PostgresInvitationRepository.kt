package com.logisty.core.adapter.outbound

import com.logisty.core.application.persistence.tables.Fleets
import com.logisty.core.application.persistence.tables.Invitations
import com.logisty.core.domain.model.Invitation
import com.logisty.core.domain.model.values.FirstName
import com.logisty.core.domain.model.values.FleetId
import com.logisty.core.domain.model.values.FleetName
import com.logisty.core.domain.model.values.InvitationId
import com.logisty.core.domain.model.values.InvitationStatus
import com.logisty.core.domain.model.values.InvitationStatus.ACCEPTED
import com.logisty.core.domain.model.values.InvitationStatus.PENDING
import com.logisty.core.domain.model.values.LastName
import com.logisty.core.domain.model.values.UserEmail
import com.logisty.core.domain.port.InvitationRepository
import org.jetbrains.exposed.sql.ResultRow
import org.jetbrains.exposed.sql.insert
import org.jetbrains.exposed.sql.selectAll
import org.jetbrains.exposed.sql.update
import org.springframework.stereotype.Repository
import java.time.Instant
import java.time.temporal.ChronoUnit

@Repository
class PostgresInvitationRepository : InvitationRepository {
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
        fleetId: FleetId,
        email: UserEmail,
        firstName: FirstName,
        lastName: LastName,
        createdAt: Instant,
    ): InvitationId {
        val invitationId = InvitationId.generate()
        val expiresAt = createdAt.plus(7, ChronoUnit.DAYS)

        Invitations.insert {
            it[Invitations.invitationId] = invitationId.value
            it[Invitations.fleetId] = fleetId.value
            it[Invitations.email] = email.value
            it[Invitations.firstName] = firstName.value
            it[Invitations.lastName] = lastName.value
            it[Invitations.status] = PENDING.name
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
}

private fun ResultRow.toInvitation(): Invitation =
    Invitation(
        invitationId = InvitationId(this[Invitations.invitationId]),
        fleetId = FleetId(this[Invitations.fleetId]),
        fleetName = FleetName(this[Fleets.fleetName]),
        email = UserEmail(this[Invitations.email]),
        firstName = FirstName(this[Invitations.firstName]),
        lastName = LastName(this[Invitations.lastName]),
        status = InvitationStatus.valueOf(this[Invitations.status]),
        createdAt = this[Invitations.createdAt],
        expiresAt = this[Invitations.expiresAt],
        acceptedAt = this[Invitations.acceptedAt],
    )
