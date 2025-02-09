package com.logisty.core.domain.model

import com.logisty.core.domain.model.values.FleetId
import com.logisty.core.domain.model.values.OrderId
import com.logisty.core.domain.model.values.OrderRouteId
import com.logisty.core.domain.model.values.OrderStepId
import com.logisty.core.domain.model.values.UserId
import org.postgis.LineString
import org.postgis.Point
import java.time.Instant

data class OrderStep(
    val orderStepId: OrderStepId,
    val description: String,
    val location: Point,
    val estimatedStartedAt: Instant,
    val estimatedEndedAt: Instant,
)

data class OrderRoute(
    val orderRouteId: OrderRouteId,
    val orderId: OrderId,
    val route: LineString,
    val duration: Double,
    val distance: Double,
)

data class Order(
    val orderId: OrderId,
    val fleetId: FleetId,
    val driverId: UserId,
    val steps: List<OrderStep>,
    val route: OrderRoute,
    val createdBy: UserId,
    val createdAt: Instant,
    val estimatedStartedAt: Instant,
    val estimatedEndedAt: Instant,
)
