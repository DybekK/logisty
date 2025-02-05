package com.logisty.core.domain.model.event

import java.util.UUID

@JvmInline
value class InternalEventId(
    val value: UUID,
) {
    companion object {
        fun generate(): InternalEventId = InternalEventId(UUID.randomUUID())
    }
}
