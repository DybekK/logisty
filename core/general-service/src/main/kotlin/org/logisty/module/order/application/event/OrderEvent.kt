package org.logisty.module.order.application.event

import kotlinx.serialization.Polymorphic
import kotlinx.serialization.Serializable
import org.logisty.infrastructure.es.Event

@Polymorphic
@Serializable
sealed interface OrderEvent : Event {
    companion object {
        const val ORDER_CREATED = "order-created"
    }
}