package com.logisty.core.domain.port

import com.logisty.core.domain.model.event.InternalEvent
import com.logisty.core.domain.model.values.FleetId
import java.time.Instant

interface EventStore {
    fun append(event: InternalEvent)

    fun findSince(
        fleetId: FleetId,
        timestamp: Instant,
    ): List<InternalEvent>
}
