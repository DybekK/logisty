package org.logisty.domain.model

import com.mongodb.client.model.geojson.Point
import org.bson.types.ObjectId
import java.time.LocalDateTime

data class Order(
    val id: ObjectId,
    val startPoint: Point,
    val endPoint: Point,
    val createdAt: LocalDateTime
)