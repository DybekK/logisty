package org.logisty.application.event

import org.logisty.domain.model.CoordinatePoint

data class OrderCreated(
    val orderId: String,
    val startPoint: CoordinatePoint,
    val endPoint: CoordinatePoint
) : OrderEvent {
    override fun eventType(): String = "order-created"
}
