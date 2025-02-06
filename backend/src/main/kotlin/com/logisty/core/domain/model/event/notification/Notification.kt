package com.logisty.core.domain.model.event.notification

import com.logisty.core.domain.model.event.InternalEventId
import com.logisty.core.domain.model.event.InternalEventType
import java.time.Instant

@JvmInline
value class NotificationTitle(
    val value: String,
)

@JvmInline
value class NotificationMessage(
    val value: String,
)

data class Notification(
    val eventId: InternalEventId,
    val title: NotificationTitle,
    val message: NotificationMessage,
    val eventType: InternalEventType,
    val notificationType: NotificationType,
    val appendedAt: Instant,
)
