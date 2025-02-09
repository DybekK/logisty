package com.logisty.core.domain.model.query

import com.logisty.core.domain.model.values.FleetId

data class GetOrdersQuery(
    val fleetId: FleetId,
    val page: Int,
    val limit: Int,
)
