package com.logisty.core.domain.service.order

import com.logisty.core.domain.model.ExtendedOrder
import com.logisty.core.domain.model.Order
import com.logisty.core.domain.model.event.OrderCancelledEvent
import com.logisty.core.domain.model.event.OrderCancelledEvent.OrderCancelledPayload
import com.logisty.core.domain.model.query.GetOrderQuery
import com.logisty.core.domain.model.query.GetOrdersQuery
import com.logisty.core.domain.model.values.OrderStatus
import com.logisty.core.domain.port.EventStore
import com.logisty.core.domain.port.OrderRepository
import com.logisty.core.domain.port.UserRepository
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import java.time.Clock

@Service
@Transactional
class OrderService(
    private val clock: Clock,
    private val eventStore: EventStore,
    private val orderRepository: OrderRepository,
    private val userRepository: UserRepository,
) {
    fun getUpcomingOrActiveOrder(query: GetOrderQuery): Order? = orderRepository.findUpcomingOrActiveOrder(query)

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

    fun cancelOrders() =
        orderRepository
            .findOrdersByStatus(OrderStatus.ASSIGNED)
            .filter { it.estimatedStartedAt < clock.instant() }
            .map { order ->
                orderRepository
                    .cancelOrder(order.orderId)
                    .also { eventStore.append(order.toOrderCancelledEvent(clock)) }
            }
}

private fun Order.toOrderCancelledEvent(clock: Clock): OrderCancelledEvent =
    OrderCancelledEvent(
        fleetId = fleetId,
        payload = OrderCancelledPayload(orderId),
        appendedAt = clock.instant(),
    )
