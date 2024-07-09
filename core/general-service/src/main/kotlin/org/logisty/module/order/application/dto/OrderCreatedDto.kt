package org.logisty.module.order.application.dto

import kotlinx.datetime.LocalDateTime
import kotlinx.serialization.Serializable
import org.logisty.module.order.domain.event.OrderCreated

@Serializable
data class OrderCreatedDto(val orderId: String, val createdAt: LocalDateTime)

fun OrderCreated.toDto(): OrderCreatedDto =
    OrderCreatedDto(orderId = orderId, createdAt = createdAt)