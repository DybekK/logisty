package org.logisty.domain

import com.eventstore.dbclient.AppendToStreamOptions
import com.eventstore.dbclient.ExpectedRevision
import org.bson.types.ObjectId
import org.logisty.application.command.CreateOrderCommand
import org.logisty.application.event.OrderCreated
import org.logisty.infrastructure.EventStore

class OrderService(private val eventStore: EventStore) {
    private val orderStream = "order-stream"

    suspend fun createOrder(command: CreateOrderCommand) {
        val orderCreated = OrderCreated(ObjectId().toString(), command.startPoint, command.endPoint)

        val options = AppendToStreamOptions.get()
            .expectedRevision(ExpectedRevision.any())

        eventStore.appendEvent(orderStream, orderCreated, options)
    }
}