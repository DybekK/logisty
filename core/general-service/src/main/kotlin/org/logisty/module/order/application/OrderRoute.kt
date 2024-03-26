package org.logisty.module.order.application

import io.ktor.server.application.*
import io.ktor.server.request.*
import io.ktor.server.response.*
import io.ktor.server.routing.*
import org.logisty.module.order.application.command.CreateOrderCommand

fun Application.ordersRouting(
    commandHandler: OrderCommandHandler,
    queryHandler: OrderQueryHandler,
) {
    routing {
        get("/orders") { call.respond(queryHandler.handleFindAll()) }
        post("/orders") {
            try {
                call.respond(commandHandler.handleCreateOrder(call.receive<CreateOrderCommand>()))
            } catch (e: Exception) {
                call.respond("Error: ${e.message}")
            }
        }
    }
}