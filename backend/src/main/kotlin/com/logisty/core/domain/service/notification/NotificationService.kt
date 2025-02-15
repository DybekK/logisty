package com.logisty.core.domain.service.notification

import com.logisty.core.application.security.AuthService
import com.logisty.core.domain.model.User
import com.logisty.core.domain.model.event.InternalEvent
import com.logisty.core.domain.model.event.notification.Notification
import com.logisty.core.domain.model.query.GetNotificationsQuery
import com.logisty.core.domain.port.EventStore
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Service
@Transactional
class NotificationService(
    private val eventStore: EventStore,
    private val notificationMapper: NotificationMapper,
    private val authService: AuthService,
) {
    fun getNotifications(query: GetNotificationsQuery): List<Notification> =
        eventStore
            .findSince(query.fleetId, query.timestamp)
            .limitVisibility(getUser(query.authorizationHeader))
            .map { notificationMapper.toNotification(query.locale, it) }

    private fun getUser(authorizationHeader: String) = authService.getCurrentUser(authorizationHeader)
}

private fun List<InternalEvent>.limitVisibility(user: User) =
    filter { event ->
        event.type.visibleTo
            .any { user.roles.contains(it) } &&
            event.visibleFor(user.userId)
    }
