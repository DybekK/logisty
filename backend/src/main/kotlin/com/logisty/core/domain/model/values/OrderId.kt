package com.logisty.core.domain.model.values

import java.util.UUID

@JvmInline
value class OrderStepId(
    val value: UUID,
) {
    companion object {
        fun generate(): OrderStepId = OrderStepId(UUID.randomUUID())
    }
}

@JvmInline
value class OrderRouteId(
    val value: UUID,
) {
    companion object {
        fun generate(): OrderRouteId = OrderRouteId(UUID.randomUUID())
    }
}

@JvmInline
value class OrderId(
    val value: UUID,
) {
    companion object {
        fun generate(): OrderId = OrderId(UUID.randomUUID())
    }
}
