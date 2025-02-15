package com.logisty.core.domain.model.command

import com.logisty.core.domain.model.values.FleetId
import com.logisty.core.domain.model.values.OrderId
import com.logisty.core.domain.model.values.OrderStepId
import org.postgis.Point
import java.time.Instant

data class ReportOrderCommand(
    val fleetId: FleetId,
    val orderId: OrderId,
    val stepId: OrderStepId,
    val actualArrivalAt: Instant,
    val location: Point,
)
