package com.logisty.core.domain.model.command

import com.logisty.core.domain.model.values.FleetId
import com.logisty.core.domain.model.values.UserId
import org.postgis.LineString
import org.postgis.Point
import java.time.Instant

data class CreateOrderCommand(
    val fleetId: FleetId,
    val driverId: UserId,
    val steps: List<OrderStep>,
    val route: OrderRoute,
    val createdBy: UserId,
    val estimatedStartedAt: Instant,
    val estimatedEndedAt: Instant,
) {
    data class OrderStep(
        val description: String,
        val location: Point,
        val estimatedStartedAt: Instant,
        val estimatedEndedAt: Instant,
    )

    data class OrderRoute(
        val route: LineString,
        val duration: Double,
        val distance: Double,
    )
}
