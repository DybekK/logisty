package com.logisty.core.adapter.inbound

import com.logisty.core.adapter.toBadRequestResponseEntity
import com.logisty.core.adapter.toInternalServerErrorResponseEntity
import com.logisty.core.domain.BusinessExceptions.FleetNotFoundException
import com.logisty.core.domain.BusinessExceptions.OrderEstimatedStartTimeAfterEndTimeException
import com.logisty.core.domain.BusinessExceptions.StepEstimatedArrivalTimeInFutureException
import com.logisty.core.domain.BusinessExceptions.UserIsNotDispatcherException
import com.logisty.core.domain.BusinessExceptions.UserIsNotDriverException
import com.logisty.core.domain.BusinessExceptions.UserNotFoundException
import com.logisty.core.domain.hub.OrderHub
import com.logisty.core.domain.model.ExtendedOrder
import com.logisty.core.domain.model.OrderRoute
import com.logisty.core.domain.model.OrderStep
import com.logisty.core.domain.model.command.CreateOrderCommand
import com.logisty.core.domain.model.query.GetOrdersQuery
import com.logisty.core.domain.model.values.FirstName
import com.logisty.core.domain.model.values.FleetId
import com.logisty.core.domain.model.values.LastName
import com.logisty.core.domain.model.values.OrderId
import com.logisty.core.domain.model.values.UserId
import org.postgis.LineString
import org.postgis.Point
import org.slf4j.LoggerFactory
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RequestParam
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
        val lat: Double,
        val lon: Double,
        val estimatedArrivalAt: Instant?,
    ) {
        fun toOrderStep() =
            CreateOrderCommand.OrderStep(
                description = description,
                location = Point(lon, lat),
                estimatedArrivalAt = estimatedArrivalAt,
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

data class CreateOrderResponse(
    val orderId: OrderId,
)

// get orders
data class GetOrderResponse(
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
) {
    data class OrderStep(
        val description: String,
        val location: Point,
        val estimatedArrivalAt: Instant?,
        val actualArrivalAt: Instant?,
    )

    data class OrderRoute(
        val route: LineString,
        val duration: Double,
        val distance: Double,
    )
}

fun OrderStep.toGetOrderStep() =
    GetOrderResponse.OrderStep(
        description = description,
        location = location,
        estimatedArrivalAt = estimatedArrivalAt,
        actualArrivalAt = actualArrivalAt,
    )

fun OrderRoute.toGetOrderRoute() =
    GetOrderResponse.OrderRoute(
        route = route,
        duration = duration,
        distance = distance,
    )

fun ExtendedOrder.toGetOrderResponse() =
    GetOrderResponse(
        orderId = orderId,
        fleetId = fleetId,
        driverId = driverId,
        driverFirstName = driverFirstName,
        driverLastName = driverLastName,
        steps = steps.map { it.toGetOrderStep() },
        route = route.toGetOrderRoute(),
        createdBy = createdBy,
        createdAt = createdAt,
        estimatedStartedAt = estimatedStartedAt,
        estimatedEndedAt = estimatedEndedAt,
    )

data class GetOrdersResponse(
    val orders: List<GetOrderResponse>,
    val total: Long,
)

@RestController
@RequestMapping("api/fleets")
class OrderController(
    private val orderHub: OrderHub,
) {
    private val logger = LoggerFactory.getLogger(OrderController::class.java)

    @PostMapping("/{fleetId}/orders")
    fun createOrder(
        @PathVariable fleetId: FleetId,
        @RequestBody request: CreateOrderRequest,
    ) = runCatching { orderHub.createOrder(request.toCreateOrderCommand(fleetId)) }
        .map { ResponseEntity.ok(CreateOrderResponse(it)) }
        .getOrElse {
            when (it) {
                is FleetNotFoundException,
                is UserNotFoundException,
                is UserIsNotDispatcherException,
                is UserIsNotDriverException,
                is StepEstimatedArrivalTimeInFutureException,
                is OrderEstimatedStartTimeAfterEndTimeException,
                -> it.toBadRequestResponseEntity()
                else -> it.toInternalServerErrorResponseEntity(logger)
            }
        }

    @GetMapping("/{fleetId}/orders")
    fun getOrders(
        @PathVariable fleetId: FleetId,
        @RequestParam(defaultValue = "0") page: Int,
        @RequestParam(defaultValue = "10") limit: Int,
    ) = runCatching { orderHub.getOrders(GetOrdersQuery(fleetId, page, limit)) }
        .map { (orders, total) ->
            ResponseEntity.ok(
                GetOrdersResponse(
                    orders = orders.map { it.toGetOrderResponse() },
                    total = total,
                ),
            )
        }.getOrElse { it.toInternalServerErrorResponseEntity(logger) }

    @GetMapping("/{fleetId}/orders/drivers/{driverId}")
    fun getDriversOrders(
        @PathVariable fleetId: FleetId,
        @PathVariable driverId: UserId,
        @RequestParam(defaultValue = "0") page: Int,
        @RequestParam(defaultValue = "10") limit: Int,
    ) = runCatching { orderHub.getOrders(GetOrdersQuery(fleetId, page, limit, driverId)) }
        .map { (orders, total) ->
            ResponseEntity.ok(
                GetOrdersResponse(
                    orders = orders.map { it.toGetOrderResponse() },
                    total = total,
                ),
            )
        }.getOrElse { it.toInternalServerErrorResponseEntity(logger) }
}
