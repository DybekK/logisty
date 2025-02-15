package com.logisty.core.domain.model.query

import com.logisty.core.domain.model.values.FleetId
import com.logisty.core.domain.model.values.UserEmail
import java.time.Instant

data class GetAvailableDriversQuery(
    val fleetId: FleetId,
    val startAt: Instant,
    val endAt: Instant,
    val email: UserEmail? = null,
)
