package com.logisty.core.domain.model.event

@JvmInline
value class InternalEventType(
    val value: String,
)

enum class FleetEventType(
    val value: InternalEventType,
) {
    FLEET_CREATED(InternalEventType("FLEET_CREATED")),
}

enum class InvitationEventType(
    val value: InternalEventType,
) {
    INVITATION_CREATED(InternalEventType("INVITATION_CREATED")),
    INVITATION_ACCEPTED(InternalEventType("INVITATION_ACCEPTED")),
    INVITATION_EXPIRED(InternalEventType("INVITATION_EXPIRED")),
}
