package org.logisty.module.order.application.event

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import org.logisty.module.order.domain.model.CoordinatePoint
import kotlinx.datetime.LocalDateTime


@Serializable
@SerialName(OrderEvent.ORDER_CREATED)
data class OrderCreated(
    val orderId: String,
    val startPoint: CoordinatePoint,
    val endPoint: CoordinatePoint,
    val createdAt: LocalDateTime
) : OrderEvent {
    override fun eventType(): String = OrderEvent.ORDER_CREATED
}