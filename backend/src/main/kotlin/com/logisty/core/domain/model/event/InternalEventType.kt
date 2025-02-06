package com.logisty.core.domain.model.event


enum class InternalEventType {
    // fleet
    FLEET_CREATED,

    // invitation
    INVITATION_CREATED,
    INVITATION_ACCEPTED,
    INVITATION_EXPIRED,
}
