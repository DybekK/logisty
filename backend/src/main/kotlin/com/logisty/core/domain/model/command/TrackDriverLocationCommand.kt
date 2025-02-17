package com.logisty.core.domain.model.command

import com.logisty.core.domain.model.values.FleetId
import com.logisty.core.domain.model.values.OrderId
import org.postgis.LineString

data class TrackDriverLocationCommand(
    val fleetId: FleetId,
    val orderId: OrderId,
    val route: LineString,
)
