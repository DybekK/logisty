package org.logisty.module.order.application.dto

import kotlinx.serialization.Serializable
import org.logisty.module.order.domain.model.Location
import org.logisty.module.order.domain.model.OrderStep
import org.logisty.module.order.domain.model.Point

@Serializable
data class OrderStepDto(
    val location: Location,
    val coordinates: List<Double>
) {
    fun toModel(): OrderStep =
        OrderStep(location = location, point = Point(coordinates = coordinates))
}

fun OrderStep.toDto(): OrderStepDto =
    OrderStepDto(location = location, coordinates = point.coordinates)
