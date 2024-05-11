package org.logisty.module.order.application.dto

import kotlinx.serialization.Serializable
import org.logisty.module.order.domain.model.Order

@Serializable
data class PagedOrdersDto(val orders: List<OrderDto>, val total: Long)

fun Pair<List<Order>, Long>.toPagedDto(): PagedOrdersDto =
    PagedOrdersDto(
        orders = first.map { it.toDto() },
        total = second
    )