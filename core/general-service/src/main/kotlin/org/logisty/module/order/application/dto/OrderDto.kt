package org.logisty.module.order.application.dto

import kotlinx.datetime.LocalDateTime
import kotlinx.datetime.toKotlinLocalDateTime
import kotlinx.serialization.Serializable
import org.logisty.module.order.domain.model.Order

@Serializable
data class OrderDto(
    val id: String,
    val steps: List<OrderStepDto>,
    val createdAt: LocalDateTime
)

fun Order.toDto(): OrderDto =
    OrderDto(
        id = id.toHexString(),
        steps = steps.map { it.toDto() },
        createdAt = createdAt.toKotlinLocalDateTime()
    )
