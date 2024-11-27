package com.logisty.core.domain.model.values

import java.util.UUID

@JvmInline
value class FleetId(
    val value: UUID,
) {
    companion object {
        fun generate(): FleetId = FleetId(UUID.randomUUID())
    }
}
