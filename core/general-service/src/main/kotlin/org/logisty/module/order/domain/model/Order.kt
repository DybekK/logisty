package org.logisty.module.order.domain.model

import kotlinx.serialization.Serializable
import org.bson.codecs.pojo.annotations.BsonId
import org.bson.types.ObjectId
import java.time.LocalDateTime

data class Order(
    @BsonId
    val id: ObjectId,
    val status: OrderStatus,
    val steps: List<OrderStep>,
    val createdAt: LocalDateTime,
)

@Serializable
enum class OrderStatus {
    CREATED,
    ACCEPTED,
    IN_PROGRESS,
    COMPLETED,
    CANCELED,
}