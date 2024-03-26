package org.logisty.module.order.domain.model

import com.mongodb.client.model.geojson.Point
import org.bson.codecs.pojo.annotations.BsonId
import org.bson.types.ObjectId
import java.time.LocalDateTime

data class Order(
    @BsonId
    val id: ObjectId,
    val startPoint: Point,
    val endPoint: Point,
    val createdAt: LocalDateTime
)