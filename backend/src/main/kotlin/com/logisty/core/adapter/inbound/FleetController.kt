package com.logisty.core.adapter.inbound

import com.logisty.core.adapter.toBadRequestResponseEntity
import com.logisty.core.adapter.toInternalServerErrorResponseEntity
import com.logisty.core.domain.BusinessExceptions.FleetAlreadyExistsException
import com.logisty.core.domain.BusinessExceptions.FleetNotFoundException
import com.logisty.core.domain.BusinessExceptions.InvitationAlreadyAcceptedException
import com.logisty.core.domain.BusinessExceptions.InvitationAlreadyExistsException
import com.logisty.core.domain.BusinessExceptions.InvitationExpiredException
import com.logisty.core.domain.BusinessExceptions.InvitationNotFoundException
import com.logisty.core.domain.BusinessExceptions.UserAlreadyExistsException
import com.logisty.core.domain.hub.FleetHub
import com.logisty.core.domain.model.values.FirstName
import com.logisty.core.domain.model.values.FleetId
import com.logisty.core.domain.model.values.FleetName
import com.logisty.core.domain.model.values.InvitationId
import com.logisty.core.domain.model.values.LastName
import com.logisty.core.domain.model.values.UserEmail
import com.logisty.core.domain.model.values.UserId
import com.logisty.core.domain.model.values.UserPassword
import org.slf4j.LoggerFactory
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

// create fleet
data class CreateFleetRequest(
    val fleetName: FleetName,
)

data class CreateFleetResponse(
    val fleetId: FleetId,
)

// create invitation
data class CreateInvitationRequest(
    val email: UserEmail,
    val firstName: FirstName,
    val lastName: LastName,
)

data class CreateInvitationResponse(
    val invitationId: InvitationId,
)

// accept invitation
data class AcceptInvitationRequest(
    val password: UserPassword,
)

data class AcceptInvitationResponse(
    val userId: UserId,
)

@RestController
@RequestMapping("api/fleets")
class FleetController(
    private val fleetHub: FleetHub,
) {
    private val logger = LoggerFactory.getLogger(FleetController::class.java)

    @PostMapping("/create")
    fun createFleet(
        @RequestBody request: CreateFleetRequest,
    ) = runCatching { fleetHub.createFleet(request.fleetName) }
        .map { ResponseEntity.ok(CreateFleetResponse(it)) }
        .getOrElse {
            when (it) {
                is FleetAlreadyExistsException -> it.toBadRequestResponseEntity()
                else -> it.toInternalServerErrorResponseEntity(logger)
            }
        }

    @PostMapping("/invite/{fleetId}")
    fun createInvitation(
        @PathVariable fleetId: FleetId,
        @RequestBody request: CreateInvitationRequest,
    ) = runCatching { fleetHub.createInvitation(fleetId, request.email, request.firstName, request.lastName) }
        .map { ResponseEntity.ok(CreateInvitationResponse(it)) }
        .getOrElse {
            when (it) {
                is FleetNotFoundException,
                is UserAlreadyExistsException,
                is InvitationAlreadyExistsException,
                -> it.toBadRequestResponseEntity()

                else -> it.toInternalServerErrorResponseEntity(logger)
            }
        }

    @PostMapping("/accept/{invitationId}")
    fun acceptInvitation(
        @PathVariable invitationId: InvitationId,
        @RequestBody request: AcceptInvitationRequest,
    ) = runCatching { fleetHub.acceptInvitation(invitationId, request.password) }
        .map { ResponseEntity.ok(AcceptInvitationResponse(it)) }
        .getOrElse {
            when (it) {
                is InvitationNotFoundException,
                is InvitationAlreadyAcceptedException,
                is InvitationExpiredException,
                -> it.toBadRequestResponseEntity()
                else -> it.toInternalServerErrorResponseEntity(logger)
            }
        }
}
