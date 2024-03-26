package org.logisty.module.order.domain.model

import kotlinx.serialization.Serializable

@Serializable
data class CoordinatePoint(val latitude: Double, val longitude: Double)