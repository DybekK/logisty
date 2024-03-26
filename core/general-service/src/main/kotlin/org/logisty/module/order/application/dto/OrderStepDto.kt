package org.logisty.module.order.application.dto


import kotlinx.serialization.Serializable
import org.logisty.module.order.domain.model.OrderStep
import org.logisty.module.order.domain.model.Point

@Serializable
data class OrderStepDto(val coordinates: List<Double>) {
    fun toModel(): OrderStep =
        OrderStep(point = Point(coordinates = coordinates))
}

fun OrderStep.toDto(): OrderStepDto =
    OrderStepDto(coordinates = point.coordinates)
