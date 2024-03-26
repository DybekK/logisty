package org.logisty.module.order.domain.model

import org.bson.codecs.pojo.annotations.BsonId
import org.bson.types.ObjectId
import java.time.LocalDateTime

data class Order(
    @BsonId
    val id: ObjectId,
    val steps: List<OrderStep>,
    val createdAt: LocalDateTime,
)