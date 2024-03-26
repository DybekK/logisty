package org.logisty.module.order.domain

import kotlinx.datetime.Clock.System.now
import kotlinx.datetime.TimeZone
import kotlinx.datetime.toLocalDateTime
import org.bson.types.ObjectId
import org.logisty.infrastructure.es.Event
import org.logisty.infrastructure.es.EventStore
import org.logisty.module.order.application.event.OrderCreated
import org.logisty.module.order.domain.model.CoordinatePoint

class OrderService(private val eventStore: EventStore) {
    private val orderStream = "order-stream"

    suspend fun createOrder(startPoint: CoordinatePoint, endPoint: CoordinatePoint): OrderCreated {
        val createdAt = now().toLocalDateTime(TimeZone.UTC)
        val orderCreated = OrderCreated(ObjectId().toString(), startPoint, endPoint, createdAt)

        eventStore.appendEvent(orderStream, orderCreated as Event)
        return orderCreated
    }
}