package org.logisty.module.order.domain.model

import kotlinx.serialization.Serializable

@Serializable
data class OrderStep(
    val location: Location,
    val point: Point
)