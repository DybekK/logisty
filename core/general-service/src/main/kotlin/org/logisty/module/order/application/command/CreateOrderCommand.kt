package org.logisty.module.order.application.command

import kotlinx.serialization.Serializable
import org.logisty.module.order.application.dto.OrderStepDto

@Serializable
data class CreateOrderCommand(val steps: List<OrderStepDto>) : OrderCommand