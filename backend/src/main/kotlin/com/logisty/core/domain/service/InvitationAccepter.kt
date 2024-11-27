package com.logisty.core.domain.service

import com.logisty.core.domain.BusinessExceptions.InvitationAlreadyAcceptedException
import com.logisty.core.domain.BusinessExceptions.InvitationExpiredException
import com.logisty.core.domain.BusinessExceptions.InvitationNotFoundException
import com.logisty.core.domain.model.Invitation
import com.logisty.core.domain.model.values.InvitationId
import com.logisty.core.domain.model.values.InvitationStatus
import com.logisty.core.domain.model.values.UserId
import com.logisty.core.domain.model.values.UserPassword
import com.logisty.core.domain.port.InvitationRepository
import com.logisty.core.domain.port.UserRepository
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import java.time.Clock

@Service
@Transactional
class InvitationAccepter(
    private val clock: Clock,
    private val userRepository: UserRepository,
    private val invitationRepository: InvitationRepository,
) {
    fun accept(invitationId: InvitationId, password: UserPassword): UserId =
        getInvitation(invitationId).let {
            it.validateInvitationStatus()
            it.validateInvitationExpiration()

            invitationRepository.acceptInvitation(invitationId)
            userRepository.createUser(it.fleetId, it.firstName, it.lastName, it.email, password)
        }

    private fun getInvitation(invitationId: InvitationId) =
        invitationRepository.findInvitationById(invitationId)
            ?: throw InvitationNotFoundException()

    private fun Invitation.validateInvitationStatus() {
        if (status == InvitationStatus.ACCEPTED) {
            throw InvitationAlreadyAcceptedException()
        }
    }

    private fun Invitation.validateInvitationExpiration() {
        val now = clock.instant()

        if (expiresAt.isBefore(now)) {
            throw InvitationExpiredException()
        }
    }
}
