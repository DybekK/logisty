package com.logisty.core.domain.service.notification

import com.logisty.core.domain.model.event.InternalEvent
import com.logisty.core.domain.model.event.notification.Notification
import com.logisty.core.domain.model.values.FleetId
import com.logisty.core.domain.port.EventStore
import org.springframework.security.core.context.SecurityContextHolder.getContext
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import java.time.Instant
import java.util.Locale

@Service
@Transactional
class NotificationService(
    private val eventStore: EventStore,
    private val notificationMapper: NotificationMapper,
) {
    fun getNotifications(
        locale: Locale,
        fleetId: FleetId,
        timestamp: Instant,
    ): List<Notification> =
        eventStore
            .findSince(fleetId, timestamp)
            .limitVisibility(getUserRoles())
            .map { notificationMapper.toNotification(locale, it) }

    private fun getUserRoles() =
        getContext()
            .authentication.authorities
            .map { it.authority }
}

private fun List<InternalEvent>.limitVisibility(userRoles: List<String>) =
    filter { event ->
        event.type.visibleTo
            .any { userRoles.contains(it.name) }
    }
