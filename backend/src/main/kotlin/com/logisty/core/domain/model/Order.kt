package com.logisty.core.domain.model

import com.logisty.core.domain.model.values.FirstName
import com.logisty.core.domain.model.values.FleetId
import com.logisty.core.domain.model.values.LastName
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
    val estimatedArrivalAt: Instant?,
    val actualArrivalAt: Instant?,
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
) {
    fun toExtendedOrder(user: User): ExtendedOrder =
        ExtendedOrder(
            orderId = orderId,
            fleetId = fleetId,
            driverId = driverId,
            driverFirstName = user.firstName,
            driverLastName = user.lastName,
            steps = steps,
            route = route,
            createdBy = createdBy,
            createdAt = createdAt,
            estimatedStartedAt = estimatedStartedAt,
            estimatedEndedAt = estimatedEndedAt,
        )
}

data class ExtendedOrder(
    val orderId: OrderId,
    val fleetId: FleetId,
    val driverId: UserId,
    val driverFirstName: FirstName,
    val driverLastName: LastName,
    val steps: List<OrderStep>,
    val route: OrderRoute,
    val createdBy: UserId,
    val createdAt: Instant,
    val estimatedStartedAt: Instant,
    val estimatedEndedAt: Instant,
)
