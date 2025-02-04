package com.logisty.core.domain.port

import com.logisty.core.domain.model.Invitation
import com.logisty.core.domain.model.command.CreateInvitationCommand
import com.logisty.core.domain.model.query.GetInvitationsQuery
import com.logisty.core.domain.model.values.InvitationId
import com.logisty.core.domain.model.values.InvitationStatus
import com.logisty.core.domain.model.values.UserEmail
import java.time.Instant

interface InvitationRepository {
    fun findInvitations(query: GetInvitationsQuery): Pair<List<Invitation>, Long>

    fun findInvitationsByStatus(status: InvitationStatus): List<Invitation>

    fun findInvitationById(id: InvitationId): Invitation?

    fun findInvitationByEmail(email: UserEmail): Invitation?

    fun createInvitation(
        command: CreateInvitationCommand,
        createdAt: Instant,
    ): InvitationId

    fun acceptInvitation(invitationId: InvitationId): InvitationId

    fun expireInvitation(invitationId: InvitationId): InvitationId
}
