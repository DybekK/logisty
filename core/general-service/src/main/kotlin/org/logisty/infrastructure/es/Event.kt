package org.logisty.infrastructure.es

import kotlinx.serialization.Polymorphic
import kotlinx.serialization.json.Json
import kotlinx.serialization.modules.SerializersModule
import kotlinx.serialization.modules.polymorphic
import kotlinx.serialization.modules.subclass
import org.logisty.module.order.domain.event.OrderCreated
import org.logisty.module.order.domain.event.OrderEvent

@Polymorphic
interface Event {
    fun eventType(): String
}

object EventSerializer {
    val serializer = Json {
        serializersModule = SerializersModule {
            polymorphic(Event::class) {
                subclass(OrderCreated::class)
            }
            polymorphic(OrderEvent::class) {
                subclass(OrderCreated::class)
            }
        }
    }
}