package org.logisty.module.order.application.command

import kotlinx.serialization.Serializable
import org.logisty.module.order.domain.model.CoordinatePoint

@Serializable
data class CreateOrderCommand(val startPoint: CoordinatePoint, val endPoint: CoordinatePoint) : OrderCommand