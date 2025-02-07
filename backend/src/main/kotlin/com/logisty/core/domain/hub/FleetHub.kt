package com.logisty.core.domain.hub

import com.logisty.core.domain.model.Invitation
import com.logisty.core.domain.model.command.CreateInvitationCommand
import com.logisty.core.domain.model.query.GetInvitationsQuery
import com.logisty.core.domain.model.values.FleetId
import com.logisty.core.domain.model.values.FleetName
import com.logisty.core.domain.model.values.InvitationId
import com.logisty.core.domain.model.values.UserId
import com.logisty.core.domain.model.values.UserPassword
import com.logisty.core.domain.service.fleet.FleetCreator
import com.logisty.core.domain.service.fleet.FleetInvitator
import com.logisty.core.domain.service.fleet.InvitationAccepter
import com.logisty.core.domain.service.fleet.InvitationService
import org.springframework.stereotype.Service

@Service
class FleetHub(
    private val fleetCreator: FleetCreator,
    private val fleetInvitator: FleetInvitator,
    private val invitationService: InvitationService,
    private val invitationAccepter: InvitationAccepter,
) {
    // fleet
    fun createFleet(fleetName: FleetName): FleetId = fleetCreator.createFleet(fleetName)

    // invitation
    fun getInvitations(query: GetInvitationsQuery): Pair<List<Invitation>, Long> = invitationService.getInvitations(query)

    fun getInvitation(invitationId: InvitationId): Invitation = invitationService.getInvitation(invitationId)

    fun createInvitation(command: CreateInvitationCommand): InvitationId = fleetInvitator.invite(command)

    fun acceptInvitation(
        invitationId: InvitationId,
        password: UserPassword,
    ): UserId = invitationAccepter.accept(invitationId, password)
}
