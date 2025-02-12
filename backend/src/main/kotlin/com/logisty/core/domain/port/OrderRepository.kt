package com.logisty.core.domain.port

import com.logisty.core.domain.model.Order
import com.logisty.core.domain.model.command.CreateOrderCommand
import com.logisty.core.domain.model.query.GetOrdersQuery
import com.logisty.core.domain.model.values.OrderId
import java.time.Instant

interface OrderRepository {
    fun createOrder(
        command: CreateOrderCommand,
        createdAt: Instant,
    ): OrderId

    fun findOrders(query: GetOrdersQuery): Pair<List<Order>, Long>
}
