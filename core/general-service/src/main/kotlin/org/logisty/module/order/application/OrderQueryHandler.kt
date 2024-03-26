package org.logisty.module.order.application

import org.logisty.module.order.application.dto.OrderDto
import org.logisty.module.order.application.dto.toDto
import org.logisty.module.order.infrastructure.OrderRepository

class OrderQueryHandler(private val orderRepository: OrderRepository) {
    suspend fun handleFindAll(): List<OrderDto> =
        orderRepository.findAll().map { it.toDto() }
}