package com.logisty.core.domain.service

import com.logisty.core.domain.BusinessExceptions.InvitationNotFoundException
import com.logisty.core.domain.model.Invitation
import com.logisty.core.domain.model.values.InvitationId
import com.logisty.core.domain.port.InvitationRepository
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Service
@Transactional
class InvitationService(
    private val invitationRepository: InvitationRepository,
) {
    fun getInvitation(invitationId: InvitationId): Invitation =
        invitationRepository.findInvitationById(invitationId)
            ?: throw InvitationNotFoundException()
}
