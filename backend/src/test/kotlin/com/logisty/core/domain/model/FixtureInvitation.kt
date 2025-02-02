package com.logisty.core.domain.model

import com.logisty.core.domain.model.values.FirstName
import com.logisty.core.domain.model.values.FleetId
import com.logisty.core.domain.model.values.FleetName
import com.logisty.core.domain.model.values.InvitationId
import com.logisty.core.domain.model.values.InvitationStatus
import com.logisty.core.domain.model.values.LastName
import com.logisty.core.domain.model.values.UserEmail
import com.logisty.core.domain.model.values.UserRole
import java.time.Instant

data class FixtureInvitation(
    val invitationId: InvitationId,
    val fleetId: FleetId,
    val fleetName: FleetName,
    val firstName: FirstName,
    val lastName: LastName,
    val email: UserEmail,
    val status: InvitationStatus,
    val roles: List<UserRole>,
    val createdAt: Instant,
    val expiresAt: Instant,
    val acceptedAt: Instant?,
)
