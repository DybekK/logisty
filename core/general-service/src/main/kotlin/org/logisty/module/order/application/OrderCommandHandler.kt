package org.logisty.module.order.application

import org.logisty.module.order.application.command.CreateOrderCommand
import org.logisty.module.order.application.dto.OrderCreatedDto
import org.logisty.module.order.application.dto.toDto
import org.logisty.module.order.domain.OrderService

class OrderCommandHandler(private val orderService: OrderService) {
    suspend fun handleCreateOrder(command: CreateOrderCommand): OrderCreatedDto {
        val steps = command.steps.map { it.toModel() }
        return orderService.createOrder(steps)
            .toDto()
    }
}