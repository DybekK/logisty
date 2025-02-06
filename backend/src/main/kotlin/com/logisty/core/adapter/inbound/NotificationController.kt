package com.logisty.core.adapter.inbound

import com.logisty.core.adapter.toInternalServerErrorResponseEntity
import com.logisty.core.domain.model.event.InternalEventId
import com.logisty.core.domain.model.event.InternalEventType
import com.logisty.core.domain.model.event.notification.Notification
import com.logisty.core.domain.model.event.notification.NotificationMessage
import com.logisty.core.domain.model.event.notification.NotificationTitle
import com.logisty.core.domain.model.event.notification.NotificationType
import com.logisty.core.domain.model.values.FleetId
import com.logisty.core.domain.service.notification.NotificationService
import org.slf4j.LoggerFactory
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RequestParam
import org.springframework.web.bind.annotation.RestController
import java.time.Instant
import java.util.Locale

data class GetNotificationResponse(
    val eventId: InternalEventId,
    val title: NotificationTitle,
    val message: NotificationMessage,
    val eventType: InternalEventType,
    val notificationType: NotificationType,
    val appendedAt: Instant,
)

fun Notification.toGetNotificationResponse() =
    GetNotificationResponse(
        eventId = eventId,
        title = title,
        message = message,
        eventType = eventType,
        notificationType = notificationType,
        appendedAt = appendedAt,
    )

data class GetNotificationsResponse(
    val notifications: List<GetNotificationResponse>,
)

@RestController
@RequestMapping("api/fleets")
class NotificationController(
    private val notificationService: NotificationService,
) {
    private val logger = LoggerFactory.getLogger(NotificationController::class.java)

    // @GetMapping("/{fleetId}/notifications")
    // fun getNotifications(
    //     @PathVariable fleetId: FleetId,
    //     @RequestParam since: Instant,
    // ) = runCatching { eventStore.findSince(fleetId, since) }
    //     .map { ResponseEntity.ok(GetNotificationsResponse(it)) }
    //     .getOrElse { it.toInternalServerErrorResponseEntity(logger) }

    @GetMapping("/{fleetId}/notifications/translated")
    fun getNotifications(
        @PathVariable fleetId: FleetId,
        @RequestParam since: Instant,
        @RequestParam(required = false) locale: Locale?,
    ) = runCatching { notificationService.getNotifications(locale ?: Locale.getDefault(), fleetId, since) }
        .map { notifications ->
            ResponseEntity.ok(
                GetNotificationsResponse(
                    notifications = notifications.map { it.toGetNotificationResponse() },
                ),
            )
        }.getOrElse { it.toInternalServerErrorResponseEntity(logger) }
}
