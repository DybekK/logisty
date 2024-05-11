package org.logisty.module.order.application.dto

import kotlinx.datetime.LocalDateTime
import kotlinx.datetime.toKotlinLocalDateTime
import kotlinx.serialization.Serializable
import org.logisty.module.order.domain.model.Order
import org.logisty.module.order.domain.model.OrderStatus

@Serializable
data class OrderDto(
    val id: String,
    val status: OrderStatus,
    val steps: List<OrderStepDto>,
    val createdAt: LocalDateTime
)

fun Order.toDto(): OrderDto =
    OrderDto(
        id = id.toHexString(),
        status = status,
        steps = steps.map { it.toDto() },
        createdAt = createdAt.toKotlinLocalDateTime()
    )
