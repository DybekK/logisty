package com.logisty.core.domain.model.event

import com.logisty.core.domain.model.values.UserRole

enum class InternalEventType(
    val visibleTo: Set<UserRole>,
) {
    // fleet
    FLEET_CREATED(visibleTo = setOf(UserRole.DISPATCHER)),

    // invitation
    INVITATION_CREATED(visibleTo = setOf(UserRole.DISPATCHER)),
    INVITATION_ACCEPTED(visibleTo = setOf(UserRole.DISPATCHER)),
    INVITATION_EXPIRED(visibleTo = setOf(UserRole.DISPATCHER)),

    // order
    ORDER_CREATED(visibleTo = setOf(UserRole.DISPATCHER)),
    ORDER_ASSIGNED_TO_DRIVER(visibleTo = setOf(UserRole.DRIVER)),
    ORDER_REPORTED(visibleTo = setOf(UserRole.DISPATCHER)),
}
