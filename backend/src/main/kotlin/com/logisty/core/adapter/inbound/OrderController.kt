package com.logisty.core.adapter.inbound

import com.logisty.core.domain.hub.FleetHub
import com.logisty.core.domain.model.command.CreateOrderCommand
import com.logisty.core.domain.model.values.FleetId
import com.logisty.core.domain.model.values.UserId
import org.postgis.LineString
import org.postgis.Point
import org.slf4j.LoggerFactory
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController
import java.time.Instant

// create order
data class CreateOrderRequest(
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
    ) {
        fun toOrderStep() =
            CreateOrderCommand.OrderStep(
                description = description,
                location = location,
                estimatedStartedAt = estimatedStartedAt,
                estimatedEndedAt = estimatedEndedAt,
            )
    }

    data class OrderRoute(
        val route: LineString,
        val duration: Double,
        val distance: Double,
    ) {
        fun toOrderRoute() =
            CreateOrderCommand.OrderRoute(
                route = route,
                duration = duration,
                distance = distance,
            )
    }

    fun toCreateOrderCommand(fleetId: FleetId) =
        CreateOrderCommand(
            fleetId = fleetId,
            driverId = driverId,
            steps = steps.map { it.toOrderStep() },
            route = route.toOrderRoute(),
            createdBy = createdBy,
            estimatedStartedAt = estimatedStartedAt,
            estimatedEndedAt = estimatedEndedAt,
        )
}

@RestController
@RequestMapping("api/fleets")
class OrderController(
    private val fleetHub: FleetHub,
) {
    private val logger = LoggerFactory.getLogger(OrderController::class.java)

    @PostMapping("/{fleetId}/orders")
    fun createOrder(
        @PathVariable fleetId: FleetId,
        @RequestBody request: CreateOrderRequest,
    ) = ResponseEntity.ok(request.toCreateOrderCommand(fleetId))
}
