package org.logisty.module.order.infrastructure

import com.mongodb.client.model.geojson.Point
import com.mongodb.client.model.geojson.Position
import org.bson.types.ObjectId
import org.logisty.infrastructure.es.EventStore
import org.logisty.module.order.application.event.OrderCreated
import org.logisty.module.order.domain.model.Order

class OrderProjectionHandler(
    private val orderRepository: OrderRepository,
    private val eventStore: EventStore
) {
    private val orderStream = "order-stream"

    suspend fun replay() = eventStore.subscribe(orderStream) { event ->
        when (event) {
            is OrderCreated -> handleOrderCreated(event)
            else -> {
                println("Unknown event type: ${event.eventType()}")
            }
        }
    }


    private suspend fun handleOrderCreated(event: OrderCreated) {
        val points = Pair(
            Point(Position(event.startPoint.longitude, event.startPoint.latitude)),
            Point(Position(event.endPoint.longitude, event.endPoint.latitude))
        )
        val order = Order(ObjectId(event.orderId), points.first, points.second, event.createdAt)

        orderRepository.save(order)
    }
}