package com.logisty.core.domain.port

import com.logisty.core.domain.model.Order
import com.logisty.core.domain.model.command.CreateOrderCommand
import com.logisty.core.domain.model.command.ReportOrderCommand
import com.logisty.core.domain.model.query.GetOrderQuery
import com.logisty.core.domain.model.query.GetOrdersQuery
import com.logisty.core.domain.model.values.FleetId
import com.logisty.core.domain.model.values.OrderId
import com.logisty.core.domain.model.values.OrderStatus
import org.postgis.LineString
import java.time.Instant

interface OrderRepository {
    fun createOrder(
        command: CreateOrderCommand,
        createdAt: Instant,
    ): OrderId

    fun reportOrder(command: ReportOrderCommand): OrderId

    fun startOrder(orderId: OrderId): OrderId

    fun completeOrder(orderId: OrderId): OrderId

    fun cancelOrder(orderId: OrderId): OrderId

    fun updateRoute(
        fleetId: FleetId,
        orderId: OrderId,
        route: LineString,
    ): OrderId

    fun findUpcomingOrActiveOrder(query: GetOrderQuery): Order?

    fun findById(id: OrderId): Order?

    fun findOrders(query: GetOrdersQuery): Pair<List<Order>, Long>

    fun findOrdersByStatus(status: OrderStatus): List<Order>
}
