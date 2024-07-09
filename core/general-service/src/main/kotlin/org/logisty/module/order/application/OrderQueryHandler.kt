package org.logisty.module.order.application

import org.logisty.module.order.application.dto.PagedOrdersDto
import org.logisty.module.order.application.dto.toPagedDto
import org.logisty.module.order.domain.OrderService

class OrderQueryHandler(private val orderService: OrderService) {
    suspend fun handleGetOrders(page: Int, size: Int): PagedOrdersDto =
        orderService.getOrders(page, size).toPagedDto()
}