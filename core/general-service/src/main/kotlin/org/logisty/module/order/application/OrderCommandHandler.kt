package org.logisty.module.order.application

import org.logisty.module.order.application.command.CreateOrderCommand
import org.logisty.module.order.application.command.OrderCommand
import org.logisty.module.order.domain.OrderService

class OrderCommandHandler(private val orderService: OrderService) {
    suspend fun handle(command: OrderCommand) {
        when (command) {
            is CreateOrderCommand -> orderService.createOrder(command)
        }
    }
}