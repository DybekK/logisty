package org.logisty.module.order.domain

import kotlinx.datetime.Clock.System.now
import kotlinx.datetime.TimeZone
import kotlinx.datetime.toLocalDateTime
import org.bson.types.ObjectId
import org.logisty.infrastructure.es.Event
import org.logisty.infrastructure.es.EventStore
import org.logisty.module.order.domain.event.OrderCreated
import org.logisty.module.order.domain.model.Order
import org.logisty.module.order.domain.model.OrderStep
import org.logisty.module.order.infrastructure.OrderRepository

class OrderService(
    private val eventStore: EventStore,
    private val orderReadRepository: OrderRepository
) {
    private val orderStream = "order-stream"

    suspend fun createOrder(steps: List<OrderStep>): OrderCreated {
        val orderCreated = OrderCreated(
            orderId = ObjectId().toString(),
            steps = steps,
            createdAt = now().toLocalDateTime(TimeZone.UTC)
        )

        eventStore.appendEvent(orderStream, orderCreated as Event)
        return orderCreated
    }

    suspend fun getOrders(page: Int, size: Int): Pair<List<Order>, Long> =
        orderReadRepository.findPaginated(page, size)
}