package com.logisty.core.domain.service

import com.logisty.core.domain.BusinessExceptions.FleetNotFoundException
import com.logisty.core.domain.BusinessExceptions.InvitationAlreadyExistsException
import com.logisty.core.domain.BusinessExceptions.UserAlreadyExistsException
import com.logisty.core.domain.model.values.FirstName
import com.logisty.core.domain.model.values.FleetId
import com.logisty.core.domain.model.values.InvitationId
import com.logisty.core.domain.model.values.LastName
import com.logisty.core.domain.model.values.UserEmail
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
    private val fleetRepository: FleetRepository,
    private val userRepository: UserRepository,
    private val invitationRepository: InvitationRepository,
) {
    fun invite(
        fleetId: FleetId,
        email: UserEmail,
        firstName: FirstName,
        lastName: LastName,
    ): InvitationId {
        validateFleet(fleetId)
        validateUser(email)
        validateInvitation(email)

        return invitationRepository.createInvitation(fleetId, email, firstName, lastName, clock.instant())
    }

    private fun validateFleet(fleetId: FleetId) =
        fleetRepository.findById(fleetId)
            ?: throw FleetNotFoundException()

    private fun validateUser(email: UserEmail) =
        userRepository.findByEmail(email)?.also {
            throw UserAlreadyExistsException()
        }

    private fun validateInvitation(email: UserEmail) {
        val invitation = invitationRepository.findInvitationByEmail(email)
        val now = clock.instant()

        if (invitation?.expiresAt?.isAfter(now) == true) {
            throw InvitationAlreadyExistsException()
        }
    }
}
