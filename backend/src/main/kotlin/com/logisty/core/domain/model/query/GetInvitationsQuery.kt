package com.logisty.core.domain.model.query

import com.logisty.core.domain.model.values.FleetId
import com.logisty.core.domain.model.values.InvitationStatus
import com.logisty.core.domain.model.values.UserEmail

data class GetInvitationsQuery(
    val fleetId: FleetId,
    val page: Int,
    val limit: Int,
    val status: InvitationStatus? = null,
    val email: UserEmail? = null,
)
