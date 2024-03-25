package org.logisty

import io.ktor.serialization.kotlinx.json.*
import io.ktor.server.application.*
import io.ktor.server.plugins.contentnegotiation.*
import kotlinx.coroutines.launch
import org.logisty.infrastructure.Database
import org.logisty.module.order.application.OrderCommandHandler
import org.logisty.module.order.application.ordersRouting
import org.logisty.module.order.domain.OrderService
import org.logisty.infrastructure.es.EventStore
import org.logisty.module.order.infrastructure.OrderProjectionHandler
import org.logisty.module.order.infrastructure.OrderRepository

fun main(args: Array<String>) {
    io.ktor.server.netty.EngineMain.main(args)
}

fun Application.module() {
    install(ContentNegotiation) {
        json()
    }

    //stores
    val database = Database(environment)
    val eventStore = EventStore(environment)

    //repositories
    val orderRepository = OrderRepository(database)

    //services
    val orderService = OrderService(eventStore)

    //handlers
    val orderCommandHandler = OrderCommandHandler(orderService)

    launch {
        OrderProjectionHandler(orderRepository, eventStore).replay()
    }

    ordersRouting(orderCommandHandler)
}