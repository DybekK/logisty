package com.logisty.core.domain.service

import com.logisty.core.domain.BusinessExceptions.InvitationAlreadyAcceptedException
import com.logisty.core.domain.BusinessExceptions.InvitationExpiredException
import com.logisty.core.domain.BusinessExceptions.InvitationNotFoundException
import com.logisty.core.domain.model.Invitation
import com.logisty.core.domain.model.command.toCreateUserCommand
import com.logisty.core.domain.model.values.InvitationId
import com.logisty.core.domain.model.values.InvitationStatus
import com.logisty.core.domain.model.values.UserId
import com.logisty.core.domain.model.values.UserPassword
import com.logisty.core.domain.port.InvitationRepository
import com.logisty.core.domain.port.UserRepository
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Service
@Transactional
class InvitationAccepter(
    private val userRepository: UserRepository,
    private val invitationRepository: InvitationRepository,
) {
    fun accept(
        invitationId: InvitationId,
        password: UserPassword,
    ): UserId =
        getInvitation(invitationId).let {
            it.validateInvitationStatus()

            invitationRepository.acceptInvitation(invitationId)
            userRepository.createUser(it.toCreateUserCommand(password))
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
