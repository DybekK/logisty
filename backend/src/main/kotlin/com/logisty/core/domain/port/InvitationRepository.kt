package com.logisty.core.domain.port

import com.logisty.core.domain.model.Invitation
import com.logisty.core.domain.model.values.FirstName
import com.logisty.core.domain.model.values.FleetId
import com.logisty.core.domain.model.values.InvitationId
import com.logisty.core.domain.model.values.LastName
import com.logisty.core.domain.model.values.UserEmail
import com.logisty.core.domain.model.values.UserRole
import java.time.Instant

interface InvitationRepository {
    fun findInvitationById(id: InvitationId): Invitation?

    fun findInvitationByEmail(email: UserEmail): Invitation?

    fun createInvitation(
        fleetId: FleetId,
        email: UserEmail,
        firstName: FirstName,
        lastName: LastName,
        roles: List<UserRole>,
        createdAt: Instant,
    ): InvitationId

    fun acceptInvitation(invitationId: InvitationId): InvitationId
}
