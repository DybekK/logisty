package com.logisty.core.domain.service.fleet

import com.logisty.core.domain.BusinessExceptions.InvitationAlreadyAcceptedException
import com.logisty.core.domain.BusinessExceptions.InvitationExpiredException
import com.logisty.core.domain.BusinessExceptions.InvitationNotFoundException
import com.logisty.core.domain.model.Invitation
import com.logisty.core.domain.model.command.toCreateUserCommand
import com.logisty.core.domain.model.event.InvitationAcceptedEvent
import com.logisty.core.domain.model.event.InvitationAcceptedEvent.InvitationAcceptedPayload
import com.logisty.core.domain.model.values.InvitationId
import com.logisty.core.domain.model.values.InvitationStatus
import com.logisty.core.domain.model.values.UserId
import com.logisty.core.domain.model.values.UserPassword
import com.logisty.core.domain.port.EventStore
import com.logisty.core.domain.port.InvitationRepository
import com.logisty.core.domain.port.UserRepository
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import java.time.Clock

@Service
@Transactional
class InvitationAccepter(
    private val clock: Clock,
    private val eventStore: EventStore,
    private val userRepository: UserRepository,
    private val invitationRepository: InvitationRepository,
) {
    fun accept(
        invitationId: InvitationId,
        password: UserPassword,
    ): UserId =
        getInvitation(invitationId).let { invitation ->
            invitation.validateInvitationStatus()

            invitationRepository
                .acceptInvitation(invitationId)
                .run { userRepository.createUser(invitation.toCreateUserCommand(password)) }
                .also { eventStore.append(invitation.toInvitationAcceptedEvent(clock)) }
        }

    private fun getInvitation(invitationId: InvitationId) =
        invitationRepository.findInvitationById(invitationId)
            ?: throw InvitationNotFoundException()

    private fun Invitation.validateInvitationStatus() {
        if (status == InvitationStatus.ACCEPTED) {
            throw InvitationAlreadyAcceptedException()
        }

        if (status == InvitationStatus.EXPIRED) {
            throw InvitationExpiredException()
        }
    }
}

private fun Invitation.toInvitationAcceptedEvent(clock: Clock): InvitationAcceptedEvent =
    InvitationAcceptedEvent(
        fleetId = fleetId,
        appendedAt = clock.instant(),
        payload = InvitationAcceptedPayload(invitationId, email),
    )
