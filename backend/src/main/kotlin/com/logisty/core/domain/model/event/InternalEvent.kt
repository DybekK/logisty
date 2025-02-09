package com.logisty.core.domain.model.event

import com.fasterxml.jackson.annotation.JsonSubTypes
import com.fasterxml.jackson.annotation.JsonSubTypes.Type
import com.fasterxml.jackson.annotation.JsonTypeInfo
import com.fasterxml.jackson.annotation.JsonTypeInfo.As
import com.fasterxml.jackson.annotation.JsonTypeInfo.Id
import com.logisty.core.domain.model.values.FirstName
import com.logisty.core.domain.model.values.FleetId
import com.logisty.core.domain.model.values.FleetName
import com.logisty.core.domain.model.values.InvitationId
import com.logisty.core.domain.model.values.LastName
import com.logisty.core.domain.model.values.OrderId
import com.logisty.core.domain.model.values.UserEmail
import com.logisty.core.domain.model.values.UserId
import org.postgis.Point
import java.time.Instant

interface Payload

@JsonTypeInfo(use = Id.NAME, include = As.PROPERTY, property = "type")
@JsonSubTypes(
    // fleet
    Type(value = FleetCreatedEvent::class, name = "FLEET_CREATED"),
    // invitation
    Type(value = InvitationCreatedEvent::class, name = "INVITATION_CREATED"),
    Type(value = InvitationAcceptedEvent::class, name = "INVITATION_ACCEPTED"),
    Type(value = InvitationExpiredEvent::class, name = "INVITATION_EXPIRED"),
    // order
    Type(value = OrderCreatedEvent::class, name = "ORDER_CREATED"),
)
sealed interface InternalEvent {
    val fleetId: FleetId
    val eventId: InternalEventId
    val type: InternalEventType
    val payload: Payload
    val appendedAt: Instant
}

// fleet
data class FleetCreatedEvent(
    override val fleetId: FleetId,
    override val payload: FleetCreatedPayload,
    override val appendedAt: Instant,
    override val eventId: InternalEventId = InternalEventId.generate(),
) : InternalEvent {
    override val type = InternalEventType.FLEET_CREATED

    data class FleetCreatedPayload(
        val fleetId: FleetId,
        val fleetName: FleetName,
    ) : Payload
}

// invitation
data class InvitationCreatedEvent(
    override val fleetId: FleetId,
    override val payload: InvitationCreatedPayload,
    override val appendedAt: Instant,
    override val eventId: InternalEventId = InternalEventId.generate(),
) : InternalEvent {
    override val type = InternalEventType.INVITATION_CREATED

    data class InvitationCreatedPayload(
        val invitationId: InvitationId,
        val email: UserEmail,
        val firstName: FirstName,
        val lastName: LastName,
    ) : Payload
}

data class InvitationAcceptedEvent(
    override val fleetId: FleetId,
    override val payload: InvitationAcceptedPayload,
    override val appendedAt: Instant,
    override val eventId: InternalEventId = InternalEventId.generate(),
) : InternalEvent {
    override val type = InternalEventType.INVITATION_ACCEPTED

    data class InvitationAcceptedPayload(
        val invitationId: InvitationId,
        val email: UserEmail,
    ) : Payload
}

data class InvitationExpiredEvent(
    override val fleetId: FleetId,
    override val payload: InvitationExpiredPayload,
    override val appendedAt: Instant,
    override val eventId: InternalEventId = InternalEventId.generate(),
) : InternalEvent {
    override val type = InternalEventType.INVITATION_EXPIRED

    data class InvitationExpiredPayload(
        val invitationId: InvitationId,
        val email: UserEmail,
    ) : Payload
}

// order
data class OrderCreatedEvent(
    override val fleetId: FleetId,
    override val payload: OrderCreatedPayload,
    override val appendedAt: Instant,
    override val eventId: InternalEventId = InternalEventId.generate(),
) : InternalEvent {
    override val type = InternalEventType.ORDER_CREATED

    data class OrderCreatedPayload(
        val orderId: OrderId,
        val driverId: UserId,
        val steps: List<OrderStep>,
        val estimatedStartedAt: Instant,
        val estimatedEndedAt: Instant,
    ) : Payload {
        data class OrderStep(
            val description: String,
            val location: Point,
            val estimatedArrivalAt: Instant?,
            val actualArrivalAt: Instant?,
        )
    }
}
