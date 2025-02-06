package com.logisty.core.domain.service.notification

import com.logisty.core.domain.model.event.notification.Notification
import com.logisty.core.domain.model.values.FleetId
import com.logisty.core.domain.port.EventStore
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
            .map { notificationMapper.toNotification(locale, it) }
}
