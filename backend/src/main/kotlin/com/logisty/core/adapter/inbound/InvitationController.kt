package com.logisty.core.adapter.inbound

import com.logisty.core.adapter.toBadRequestResponseEntity
import com.logisty.core.adapter.toInternalServerErrorResponseEntity
import com.logisty.core.adapter.toNotFoundResponseEntity
import com.logisty.core.domain.BusinessExceptions.FleetNotFoundException
import com.logisty.core.domain.BusinessExceptions.InvitationAlreadyAcceptedException
import com.logisty.core.domain.BusinessExceptions.InvitationAlreadyExistsException
import com.logisty.core.domain.BusinessExceptions.InvitationExpiredException
import com.logisty.core.domain.BusinessExceptions.InvitationNotFoundException
import com.logisty.core.domain.BusinessExceptions.UserAlreadyExistsException
import com.logisty.core.domain.hub.FleetHub
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
import com.logisty.core.domain.model.values.LastName
import com.logisty.core.domain.model.values.PhoneNumber
import com.logisty.core.domain.model.values.PostalCode
import com.logisty.core.domain.model.values.StateProvince
import com.logisty.core.domain.model.values.Street
import com.logisty.core.domain.model.values.StreetNumber
import com.logisty.core.domain.model.values.UserEmail
import com.logisty.core.domain.model.values.UserId
import com.logisty.core.domain.model.values.UserPassword
import com.logisty.core.domain.model.values.UserRole
import org.slf4j.LoggerFactory
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RequestParam
import org.springframework.web.bind.annotation.RestController
import java.time.Instant
import java.time.LocalDate

// create invitation
data class CreateInvitationRequest(
    val email: UserEmail,
    val firstName: FirstName,
    val lastName: LastName,
    val phoneNumber: PhoneNumber,
    val dateOfBirth: LocalDate,
    val street: Street,
    val streetNumber: StreetNumber,
    val apartmentNumber: ApartmentNumber?,
    val city: City,
    val stateProvince: StateProvince,
    val postalCode: PostalCode,
    val roles: List<UserRole>,
) {
    fun toCreateInvitationCommand(fleetId: FleetId) =
        CreateInvitationCommand(
            fleetId = fleetId,
            email = email,
            firstName = firstName,
            lastName = lastName,
            phoneNumber = phoneNumber,
            dateOfBirth = dateOfBirth,
            street = street,
            streetNumber = streetNumber,
            apartmentNumber = apartmentNumber,
            city = city,
            stateProvince = stateProvince,
            postalCode = postalCode,
            roles = roles,
        )
}

data class CreateInvitationResponse(
    val invitationId: InvitationId,
)

// get invitation
data class GetInvitationResponse(
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

fun Invitation.toGetInvitationResponse() =
    GetInvitationResponse(
        invitationId = invitationId,
        fleetId = fleetId,
        fleetName = fleetName,
        firstName = firstName,
        lastName = lastName,
        email = email,
        status = status,
        roles = roles,
        createdAt = createdAt,
        expiresAt = expiresAt,
        acceptedAt = acceptedAt,
    )

// get invitations
data class GetInvitationsResponse(
    val invitations: List<GetInvitationResponse>,
    val total: Long,
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
class InvitationController(
    private val fleetHub: FleetHub,
) {
    private val logger = LoggerFactory.getLogger(InvitationController::class.java)

    @PostMapping("/{fleetId}/invite")
    fun createInvitation(
        @PathVariable fleetId: FleetId,
        @RequestBody request: CreateInvitationRequest,
    ) = runCatching { fleetHub.createInvitation(request.toCreateInvitationCommand(fleetId)) }
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

    @PostMapping("/invitations/{invitationId}/accept")
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

    @GetMapping("/invitations/{invitationId}")
    fun getInvitation(
        @PathVariable invitationId: InvitationId,
    ) = runCatching { fleetHub.getInvitation(invitationId) }
        .map { ResponseEntity.ok(it.toGetInvitationResponse()) }
        .getOrElse {
            when (it) {
                is InvitationNotFoundException -> it.toNotFoundResponseEntity()
                else -> it.toInternalServerErrorResponseEntity(logger)
            }
        }

    @GetMapping("/{fleetId}/invitations")
    fun getInvitations(
        @PathVariable fleetId: FleetId,
        @RequestParam(defaultValue = "0") page: Int,
        @RequestParam(defaultValue = "10") limit: Int,
        @RequestParam status: InvitationStatus?,
        @RequestParam email: UserEmail?,
    ) = runCatching { fleetHub.getInvitations(GetInvitationsQuery(fleetId, page, limit, status, email)) }
        .map { (invitations, total) ->
            ResponseEntity.ok(
                GetInvitationsResponse(
                    invitations = invitations.map { it.toGetInvitationResponse() },
                    total = total,
                ),
            )
        }.getOrElse { it.toInternalServerErrorResponseEntity(logger) }
}
