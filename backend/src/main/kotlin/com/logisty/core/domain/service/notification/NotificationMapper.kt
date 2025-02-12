package com.logisty.core.domain.service.notification

import com.logisty.core.domain.model.event.FleetCreatedEvent
import com.logisty.core.domain.model.event.InternalEvent
import com.logisty.core.domain.model.event.InvitationAcceptedEvent
import com.logisty.core.domain.model.event.InvitationCreatedEvent
import com.logisty.core.domain.model.event.InvitationExpiredEvent
import com.logisty.core.domain.model.event.OrderCreatedEvent
import com.logisty.core.domain.model.event.notification.Notification
import com.logisty.core.domain.model.event.notification.NotificationMessage
import com.logisty.core.domain.model.event.notification.NotificationTitle
import com.logisty.core.domain.model.event.notification.NotificationType
import org.springframework.context.MessageSource
import org.springframework.stereotype.Component
import java.util.Locale

@Component
class NotificationMapper(
    private val messageSource: MessageSource,
) {
    fun toNotification(
        locale: Locale,
        event: InternalEvent,
    ): Notification =
        when (event) {
            // fleet
            is FleetCreatedEvent -> event.toNotification(locale)

            // invitation
            is InvitationCreatedEvent -> event.toNotification(locale)
            is InvitationAcceptedEvent -> event.toNotification(locale)
            is InvitationExpiredEvent -> event.toNotification(locale)

            // order
            is OrderCreatedEvent -> event.toNotification(locale)
        }

    private fun FleetCreatedEvent.toNotification(locale: Locale): Notification =
        Notification(
            eventId = eventId,
            title =
                NotificationTitle(
                    messageSource.getMessage(
                        "event.fleet.created.title",
                        null,
                        locale,
                    ),
                ),
            message =
                NotificationMessage(
                    messageSource.getMessage(
                        "event.fleet.created.message",
                        arrayOf(payload.fleetId.value),
                        locale,
                    ),
                ),
            eventType = type,
            notificationType = NotificationType.INFO,
            appendedAt = appendedAt,
        )

    private fun InvitationCreatedEvent.toNotification(locale: Locale): Notification =
        Notification(
            eventId = eventId,
            title =
                NotificationTitle(
                    messageSource.getMessage(
                        "event.invitation.created.title",
                        null,
                        locale,
                    ),
                ),
            message =
                NotificationMessage(
                    messageSource.getMessage(
                        "event.invitation.created.message",
                        arrayOf(payload.email.value),
                        locale,
                    ),
                ),
            eventType = type,
            notificationType = NotificationType.INFO,
            appendedAt = appendedAt,
        )

    private fun InvitationAcceptedEvent.toNotification(locale: Locale): Notification =
        Notification(
            eventId = eventId,
            title =
                NotificationTitle(
                    messageSource.getMessage(
                        "event.invitation.accepted.title",
                        null,
                        locale,
                    ),
                ),
            message =
                NotificationMessage(
                    messageSource.getMessage(
                        "event.invitation.accepted.message",
                        arrayOf(payload.email.value),
                        locale,
                    ),
                ),
            eventType = type,
            notificationType = NotificationType.INFO,
            appendedAt = appendedAt,
        )

    private fun InvitationExpiredEvent.toNotification(locale: Locale): Notification =
        Notification(
            eventId = eventId,
            title =
                NotificationTitle(
                    messageSource.getMessage(
                        "event.invitation.expired.title",
                        null,
                        locale,
                    ),
                ),
            message =
                NotificationMessage(
                    messageSource.getMessage(
                        "event.invitation.expired.message",
                        arrayOf(payload.email.value),
                        locale,
                    ),
                ),
            eventType = type,
            notificationType = NotificationType.INFO,
            appendedAt = appendedAt,
        )

    private fun OrderCreatedEvent.toNotification(locale: Locale): Notification =
        Notification(
            eventId = eventId,
            title =
                NotificationTitle(
                    messageSource.getMessage(
                        "event.order.created.title",
                        null,
                        locale,
                    ),
                ),
            message =
                NotificationMessage(
                    messageSource.getMessage(
                        "event.order.created.message",
                        arrayOf(payload.orderId.value),
                        locale,
                    ),
                ),
            eventType = type,
            notificationType = NotificationType.INFO,
            appendedAt = appendedAt,
        )
}
