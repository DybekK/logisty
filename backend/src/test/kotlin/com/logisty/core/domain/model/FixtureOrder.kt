package com.logisty.core.domain.model

import com.logisty.core.adapter.inbound.CreateOrderRequest
import com.logisty.core.domain.model.values.FleetId
import com.logisty.core.domain.model.values.OrderId
import com.logisty.core.domain.model.values.OrderRouteId
import com.logisty.core.domain.model.values.OrderStepId
import com.logisty.core.domain.model.values.UserId
import org.postgis.LineString
import org.postgis.Point
import java.time.Instant

data class FixtureOrderStep(
    val orderStepId: OrderStepId,
    val description: String,
    val location: Point,
    val estimatedArrivalAt: Instant? = null,
    val actualArrivalAt: Instant? = null,
)

data class FixtureOrderRoute(
    val orderRouteId: OrderRouteId,
    val orderId: OrderId,
    val route: LineString,
    val duration: Double,
    val distance: Double,
)

data class FixtureOrder(
    val orderId: OrderId,
    val fleetId: FleetId,
    val driverId: UserId,
    val steps: List<FixtureOrderStep>,
    val route: FixtureOrderRoute,
    val createdBy: UserId,
    val createdAt: Instant,
    val estimatedStartedAt: Instant,
    val estimatedEndedAt: Instant,
) {
    fun toCreateOrderRequest() =
        CreateOrderRequest(
            driverId = driverId,
            steps =
                steps.map {
                    CreateOrderRequest.OrderStep(
                        description = it.description,
                        lat = it.location.y,
                        lon = it.location.x,
                        estimatedArrivalAt = it.estimatedArrivalAt,
                    )
                },
            route =
                CreateOrderRequest.OrderRoute(
                    route = route.route,
                    duration = route.duration,
                    distance = route.distance,
                ),
            createdBy = createdBy,
            estimatedStartedAt = estimatedStartedAt,
            estimatedEndedAt = estimatedEndedAt,
        )
}
