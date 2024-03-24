package org.logisty.domain.model

import kotlinx.serialization.Serializable

@Serializable
data class CoordinatePoint(val latitude: Double, val longitude: Double)