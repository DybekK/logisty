package com.logisty.core.domain.service.order

import com.logisty.core.domain.model.ExtendedOrder
import com.logisty.core.domain.model.Order
import com.logisty.core.domain.model.query.GetOrdersQuery
import com.logisty.core.domain.port.OrderRepository
import com.logisty.core.domain.port.UserRepository
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Service
@Transactional
class OrderService(
    private val orderRepository: OrderRepository,
    private val userRepository: UserRepository,
) {
    fun getOrders(query: GetOrdersQuery): Pair<List<Order>, Long> = orderRepository.findOrders(query)

    fun getExtendedOrders(query: GetOrdersQuery): Pair<List<ExtendedOrder>, Long> =
        getOrders(query)
            .let { (orders, total) ->
                val indexedUsers =
                    userRepository
                        .findUsers(orders.map { it.driverId })
                        .associateBy { it.userId }

                orders.mapNotNull { order ->
                    indexedUsers[order.driverId]
                        ?.let { order.toExtendedOrder(it) }
                } to total
            }
}
