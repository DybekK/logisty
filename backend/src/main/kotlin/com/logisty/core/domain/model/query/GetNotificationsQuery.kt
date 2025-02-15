package com.logisty.core.domain.model.query

import com.logisty.core.domain.model.values.FleetId
import java.time.Instant
import java.util.Locale

data class GetNotificationsQuery(
    val locale: Locale,
    val authorizationHeader: String,
    val fleetId: FleetId,
    val timestamp: Instant,
)
