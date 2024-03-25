package org.logisty.module.order.domain.model

import com.mongodb.client.model.geojson.Point
import kotlinx.datetime.LocalDateTime
import org.bson.types.ObjectId

data class Order(
    val id: ObjectId,
    val startPoint: Point,
    val endPoint: Point,
    val createdAt: LocalDateTime
)