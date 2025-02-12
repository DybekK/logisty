package com.logisty.core.domain.hub

import com.logisty.core.domain.model.ExtendedOrder
import com.logisty.core.domain.model.User
import com.logisty.core.domain.model.command.CreateOrderCommand
import com.logisty.core.domain.model.query.GetAvailableDriversQuery
import com.logisty.core.domain.model.query.GetOrdersQuery
import com.logisty.core.domain.model.values.OrderId
import com.logisty.core.domain.service.order.DriverService
import com.logisty.core.domain.service.order.OrderCreator
import com.logisty.core.domain.service.order.OrderService
import org.springframework.stereotype.Service

@Service
class OrderHub(
    private val orderCreator: OrderCreator,
    private val orderService: OrderService,
    private val driverService: DriverService,
) {
    fun createOrder(command: CreateOrderCommand): OrderId = orderCreator.createOrder(command)

    fun getOrders(query: GetOrdersQuery): Pair<List<ExtendedOrder>, Long> = orderService.getExtendedOrders(query)

    fun getAvailableDrivers(query: GetAvailableDriversQuery): List<User> = driverService.getAvailableDrivers(query)
}
