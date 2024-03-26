package org.logisty.module.order.domain

import kotlinx.datetime.Clock.System.now
import kotlinx.datetime.TimeZone
import kotlinx.datetime.toLocalDateTime
import org.bson.types.ObjectId
import org.logisty.infrastructure.es.Event
import org.logisty.infrastructure.es.EventStore
import org.logisty.module.order.domain.event.OrderCreated
import org.logisty.module.order.domain.model.OrderStep

class OrderService(private val eventStore: EventStore) {
    private val orderStream = "order-stream"

    suspend fun createOrder(steps: List<OrderStep>): OrderCreated {
        val createdAt = now().toLocalDateTime(TimeZone.UTC)
        val orderCreated = OrderCreated(ObjectId().toString(), steps, createdAt)

        eventStore.appendEvent(orderStream, orderCreated as Event)
        return orderCreated
    }
}