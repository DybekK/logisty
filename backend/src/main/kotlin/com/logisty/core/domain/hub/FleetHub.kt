package com.logisty.core.domain.hub

import com.logisty.core.domain.model.values.FirstName
import com.logisty.core.domain.model.values.FleetId
import com.logisty.core.domain.model.values.FleetName
import com.logisty.core.domain.model.values.InvitationId
import com.logisty.core.domain.model.values.LastName
import com.logisty.core.domain.model.values.UserEmail
import com.logisty.core.domain.model.values.UserId
import com.logisty.core.domain.model.values.UserPassword
import com.logisty.core.domain.service.FleetCreator
import com.logisty.core.domain.service.FleetInvitator
import com.logisty.core.domain.service.InvitationAccepter
import org.springframework.stereotype.Service

@Service
class FleetHub(
    private val fleetCreator: FleetCreator,
    private val fleetInvitator: FleetInvitator,
    private val invitationAccepter: InvitationAccepter,
) {
    fun createFleet(fleetName: FleetName): FleetId = fleetCreator.createFleet(fleetName)

    fun createInvitation(
        fleetId: FleetId,
        email: UserEmail,
        firstName: FirstName,
        lastName: LastName,
    ): InvitationId = fleetInvitator.invite(fleetId, email, firstName, lastName)

    fun acceptInvitation(
        invitationId: InvitationId,
        password: UserPassword,
    ): UserId = invitationAccepter.accept(invitationId, password)
} 
