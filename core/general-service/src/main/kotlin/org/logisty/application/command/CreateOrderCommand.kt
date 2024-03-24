package org.logisty.application.command

import kotlinx.serialization.Serializable
import org.logisty.domain.model.CoordinatePoint

@Serializable
data class CreateOrderCommand(val startPoint: CoordinatePoint, val endPoint: CoordinatePoint) : OrderCommand