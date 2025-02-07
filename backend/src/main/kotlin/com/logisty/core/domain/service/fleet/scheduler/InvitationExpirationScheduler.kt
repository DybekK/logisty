package com.logisty.core.domain.service.fleet.scheduler

import com.logisty.core.domain.service.fleet.InvitationService
import org.slf4j.LoggerFactory
import org.springframework.scheduling.annotation.Scheduled
import org.springframework.stereotype.Component

@Component
class InvitationExpirationScheduler(
    private val invitationService: InvitationService,
) {
    private val logger = LoggerFactory.getLogger(InvitationExpirationScheduler::class.java)

    @Scheduled(fixedRate = 60000)
    fun scheduleExpireInvitations() =
        invitationService
            .expireInvitations()
            .takeIf { it.isNotEmpty() }
            ?.also { logger.info("Expired {} invitations: {}", it.size, it.map { it.value }) }
}
