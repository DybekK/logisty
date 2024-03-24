package org.logisty.application

import org.logisty.application.command.CreateOrderCommand
import org.logisty.application.command.OrderCommand
import org.logisty.domain.OrderService

class OrderCommandHandler(private val orderService: OrderService) {
    suspend fun handle(command: OrderCommand) {
        when (command) {
            is CreateOrderCommand -> orderService.createOrder(command)
        }
    }
}