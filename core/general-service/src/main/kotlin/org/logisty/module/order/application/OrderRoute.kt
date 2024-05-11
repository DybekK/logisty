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
        get("/orders") {
            val page = call.parameters["page"]?.toIntOrNull() ?: 0
            val size = call.parameters["size"]?.toIntOrNull() ?: 10
            call.respond(queryHandler.handleGetOrders(page, size))
        }

        post("/orders") {
            try {
                val command = call.receive<CreateOrderCommand>()
                call.respond(commandHandler.handleCreateOrder(command))
            } catch (e: Exception) {
                call.respond("Error: ${e.message}")
            }
        }
    }
}