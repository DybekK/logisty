package com.logisty.core.domain.model.query

import com.logisty.core.domain.model.values.FleetId
import com.logisty.core.domain.model.values.UserId
import java.time.Duration
import java.time.Instant

data class GetOrderQuery(
    val fleetId: FleetId,
    val driverId: UserId,
    val nearestTo: Instant,
    val lookupRange: Duration,
)
