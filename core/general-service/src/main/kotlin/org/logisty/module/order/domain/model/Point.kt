package org.logisty.module.order.domain.model

import kotlinx.serialization.Serializable

@Serializable
data class Point(val type: String = "Point", val coordinates: List<Double>)