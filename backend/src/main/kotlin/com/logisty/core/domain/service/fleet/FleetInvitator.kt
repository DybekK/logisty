package com.logisty.core.domain.service.fleet

import com.logisty.core.domain.BusinessExceptions
import com.logisty.core.domain.model.command.CreateInvitationCommand
import com.logisty.core.domain.model.event.InvitationCreatedEvent
import com.logisty.core.domain.model.event.InvitationCreatedEvent.InvitationCreatedPayload
import com.logisty.core.domain.model.values.FleetId
import com.logisty.core.domain.model.values.InvitationId
import com.logisty.core.domain.model.values.InvitationStatus
import com.logisty.core.domain.model.values.UserEmail
import com.logisty.core.domain.port.EventStore
import com.logisty.core.domain.port.FleetRepository
import com.logisty.core.domain.port.InvitationRepository
import com.logisty.core.domain.port.UserRepository
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import java.time.Clock

@Service
@Transactional
class FleetInvitator(
    private val clock: Clock,
    private val eventStore: EventStore,
    private val fleetRepository: FleetRepository,
    private val userRepository: UserRepository,
    private val invitationRepository: InvitationRepository,
) {
    fun invite(command: CreateInvitationCommand): InvitationId {
        validateFleet(command.fleetId)
        validateUser(command.email)
        validateInvitation(command.email)

        return invitationRepository
            .createInvitation(command, clock.instant())
            .also { eventStore.append(command.toInvitationCreatedEvent(it, clock)) }
    }

    private fun validateFleet(fleetId: FleetId) =
        fleetRepository.findById(fleetId)
            ?: throw BusinessExceptions.FleetNotFoundException()

    private fun validateUser(email: UserEmail) =
        userRepository.findByEmail(email)?.also {
            throw BusinessExceptions.UserAlreadyExistsException()
        }

    private fun validateInvitation(email: UserEmail) {
        val invitation = invitationRepository.findInvitationByEmail(email)

        if (invitation?.status == InvitationStatus.PENDING) {
            throw BusinessExceptions.InvitationAlreadyExistsException()
        }
    }
}

private fun CreateInvitationCommand.toInvitationCreatedEvent(
    invitationId: InvitationId,
    clock: Clock,
): InvitationCreatedEvent =
    InvitationCreatedEvent(
        fleetId = fleetId,
        appendedAt = clock.instant(),
        payload =
            InvitationCreatedPayload(
                invitationId = invitationId,
                email = email,
                firstName = firstName,
                lastName = lastName,
            ),
    )
