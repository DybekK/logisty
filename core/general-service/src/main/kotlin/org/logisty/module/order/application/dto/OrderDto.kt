package org.logisty.module.order.application.dto

import kotlinx.datetime.LocalDateTime
import kotlinx.datetime.toKotlinLocalDateTime
import kotlinx.serialization.Serializable
import org.logisty.module.order.domain.model.CoordinatePoint
import org.logisty.module.order.domain.model.Order

@Serializable
data class OrderDto(
    val id: String,
    val startPoint: CoordinatePoint,
    val endPoint: CoordinatePoint,
    val createdAt: LocalDateTime
)

fun Order.toDto(): OrderDto = OrderDto(
    id = id.toHexString(),
    startPoint = CoordinatePoint(startPoint.position.values[1], startPoint.position.values[0]),
    endPoint = CoordinatePoint(endPoint.position.values[1], endPoint.position.values[0]),
    createdAt = createdAt.toKotlinLocalDateTime()
)
