package org.logisty.module.order.application

import io.ktor.server.application.*
import io.ktor.server.request.*
import io.ktor.server.routing.*
import org.logisty.module.order.application.command.CreateOrderCommand

fun Application.ordersRouting(commandHandler: OrderCommandHandler) {
    routing {
        post("/orders") { commandHandler.handle(call.receive<CreateOrderCommand>()) }
    }
}