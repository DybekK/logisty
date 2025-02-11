package com.logisty.core.domain.model.query

import com.logisty.core.domain.model.values.FleetId
import java.time.Instant

data class GetAvailableDriversQuery(
    val fleetId: FleetId,
    val startAt: Instant,
    val endAt: Instant,
)
