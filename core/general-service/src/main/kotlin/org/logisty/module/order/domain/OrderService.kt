package org.logisty.module.order.domain

import com.eventstore.dbclient.AppendToStreamOptions
import com.eventstore.dbclient.ExpectedRevision
import kotlinx.datetime.Clock.System.now
import kotlinx.datetime.*
import org.bson.types.ObjectId
import org.logisty.infrastructure.es.Event
import org.logisty.infrastructure.es.EventStore
import org.logisty.module.order.application.command.CreateOrderCommand
import org.logisty.module.order.application.event.OrderCreated

class OrderService(private val eventStore: EventStore) {
    private val orderStream = "order-stream"

    suspend fun createOrder(command: CreateOrderCommand) {
        val createdAt = now().toLocalDateTime(TimeZone.UTC)
        val orderCreated = OrderCreated(ObjectId().toString(), command.startPoint, command.endPoint, createdAt)

        val options = AppendToStreamOptions.get()
            .expectedRevision(ExpectedRevision.any())

        eventStore.appendEvent(orderStream, orderCreated as Event, options)
    }
}