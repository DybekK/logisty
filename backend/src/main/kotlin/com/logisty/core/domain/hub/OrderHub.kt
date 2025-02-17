package com.logisty.core.domain.hub

import com.logisty.core.domain.model.ExtendedOrder
import com.logisty.core.domain.model.Order
import com.logisty.core.domain.model.User
import com.logisty.core.domain.model.command.CreateOrderCommand
import com.logisty.core.domain.model.command.ReportOrderCommand
import com.logisty.core.domain.model.command.TrackDriverLocationCommand
import com.logisty.core.domain.model.query.GetAvailableDriversQuery
import com.logisty.core.domain.model.query.GetOrderQuery
import com.logisty.core.domain.model.query.GetOrdersQuery
import com.logisty.core.domain.model.values.OrderId
import com.logisty.core.domain.service.order.DriverLocationTracker
import com.logisty.core.domain.service.order.DriverService
import com.logisty.core.domain.service.order.OrderCreator
import com.logisty.core.domain.service.order.OrderService
import com.logisty.core.domain.service.order.reporter.OrderReporter
import org.springframework.stereotype.Service

@Service
class OrderHub(
    private val orderCreator: OrderCreator,
    private val orderService: OrderService,
    private val driverService: DriverService,
    private val orderReporter: OrderReporter,
    private val driverLocationTracker: DriverLocationTracker,
) {
    fun createOrder(command: CreateOrderCommand): OrderId = orderCreator.createOrder(command)

    fun reportOrder(command: ReportOrderCommand): OrderId = orderReporter.reportOrder(command)

    fun trackDriverLocation(command: TrackDriverLocationCommand): OrderId = driverLocationTracker.trackDriverLocation(command)

    fun getUpcomingOrActiveOrder(query: GetOrderQuery): Order? = orderService.getUpcomingOrActiveOrder(query)

    fun getOrders(query: GetOrdersQuery): Pair<List<ExtendedOrder>, Long> = orderService.getExtendedOrders(query)

    fun getAvailableDrivers(query: GetAvailableDriversQuery): List<User> = driverService.getAvailableDrivers(query)
}
