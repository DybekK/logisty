package org.logisty.module.order.infrastructure

import io.ktor.util.logging.*
import org.logisty.infrastructure.es.EventStore
import org.logisty.module.order.domain.event.OrderCreated

class OrderProjectionHandler(
    private val logger: Logger,
    private val orderRepository: OrderRepository,
    private val eventStore: EventStore
) {
    private val orderStream = "order-stream"

    suspend fun subscribe() = eventStore.subscribe(orderStream) { event ->
        when (event) {
            is OrderCreated -> projectOrderCreated(event)
            else -> logger.error("Unknown event type: ${event.eventType()}")
        }
    }

    private suspend fun projectOrderCreated(event: OrderCreated) =
        orderRepository.save(event.toOrder())
}