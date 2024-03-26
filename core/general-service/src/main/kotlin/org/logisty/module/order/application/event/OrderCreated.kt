package org.logisty.module.order.application.event

import com.mongodb.client.model.geojson.Point
import com.mongodb.client.model.geojson.Position
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import org.logisty.module.order.domain.model.CoordinatePoint
import kotlinx.datetime.LocalDateTime
import kotlinx.datetime.toJavaLocalDateTime
import org.bson.types.ObjectId
import org.logisty.module.order.domain.model.Order

@Serializable
@SerialName(OrderEvent.ORDER_CREATED)
data class OrderCreated(
    val orderId: String,
    val startPoint: CoordinatePoint,
    val endPoint: CoordinatePoint,
    val createdAt: LocalDateTime
) : OrderEvent {
    override fun eventType(): String = OrderEvent.ORDER_CREATED

    fun toOrder(): Order {
        val startPoint = Point(Position(startPoint.longitude, startPoint.latitude))
        val endPoint = Point(Position(endPoint.longitude, endPoint.latitude))
        return Order(ObjectId(orderId), startPoint, endPoint, createdAt.toJavaLocalDateTime())
    }
}