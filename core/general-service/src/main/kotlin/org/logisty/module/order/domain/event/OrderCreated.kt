package org.logisty.module.order.domain.event

import kotlinx.datetime.LocalDateTime
import kotlinx.datetime.toJavaLocalDateTime
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import org.bson.types.ObjectId
import org.logisty.module.order.domain.model.Order
import org.logisty.module.order.domain.model.OrderStatus
import org.logisty.module.order.domain.model.OrderStep

@Serializable
@SerialName(OrderEvent.ORDER_CREATED)
data class OrderCreated(
    val orderId: String,
    val steps: List<OrderStep>,
    val createdAt: LocalDateTime
) : OrderEvent {
    override fun eventType(): String = OrderEvent.ORDER_CREATED

    fun toOrder(): Order =
        Order(
            id = ObjectId(orderId),
            status = OrderStatus.CREATED,
            steps = steps,
            createdAt = createdAt.toJavaLocalDateTime()
        )
}