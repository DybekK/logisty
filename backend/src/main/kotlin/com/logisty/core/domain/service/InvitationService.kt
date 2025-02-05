package com.logisty.core.domain.service

import com.logisty.core.domain.BusinessExceptions.InvitationNotFoundException
import com.logisty.core.domain.model.Invitation
import com.logisty.core.domain.model.query.GetInvitationsQuery
import com.logisty.core.domain.model.values.InvitationId
import com.logisty.core.domain.model.values.InvitationStatus
import com.logisty.core.domain.port.InvitationRepository
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import java.time.Clock

@Service
@Transactional
class InvitationService(
    private val clock: Clock,
    private val invitationRepository: InvitationRepository,
) {
    fun getInvitation(invitationId: InvitationId): Invitation =
        invitationRepository.findInvitationById(invitationId)
            ?: throw InvitationNotFoundException()

    fun getInvitations(query: GetInvitationsQuery): Pair<List<Invitation>, Long> = invitationRepository.findInvitations(query)

    fun expireInvitations(): List<InvitationId> =
        invitationRepository
            .findInvitationsByStatus(InvitationStatus.PENDING)
            .filter { it.expiresAt <= clock.instant() }
            .map { invitationRepository.expireInvitation(it.invitationId) }
}
