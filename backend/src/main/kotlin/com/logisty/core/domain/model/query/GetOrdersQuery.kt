package com.logisty.core.domain.model.query

import com.logisty.core.domain.model.values.FleetId
import com.logisty.core.domain.model.values.UserId

data class GetOrdersQuery(
    val fleetId: FleetId,
    val page: Int,
    val limit: Int,
    val driverId: UserId? = null,
)
