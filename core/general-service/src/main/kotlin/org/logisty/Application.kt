package org.logisty

import io.ktor.serialization.kotlinx.json.*
import io.ktor.server.application.*
import io.ktor.server.plugins.contentnegotiation.*
import org.logisty.application.OrderCommandHandler
import org.logisty.application.ordersRouting
import org.logisty.domain.OrderService
import org.logisty.infrastructure.Database
import org.logisty.infrastructure.EventStore

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

    //services
    val orderService = OrderService(eventStore)
    val orderCommandHandler = OrderCommandHandler(orderService)

    ordersRouting(orderCommandHandler)
}