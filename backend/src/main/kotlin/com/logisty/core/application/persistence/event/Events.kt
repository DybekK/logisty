package com.logisty.core.application.persistence.event

import com.logisty.core.application.mapper
import com.logisty.core.domain.model.event.InternalEvent
import org.jetbrains.exposed.sql.Table
import org.jetbrains.exposed.sql.javatime.timestamp
import org.jetbrains.exposed.sql.json.jsonb

object Events : Table() {
    val fleetId = uuid("fleet_id")
    val eventId = uuid("event_id")
    val type = varchar("type", 255)
    val appendedAt = timestamp("appended_at")
    val payload =
        jsonb<InternalEvent>(
            "event_payload",
            serialize = { mapper.writeValueAsString(it) },
            deserialize = { mapper.readValue(it, InternalEvent::class.java) },
        )

    override val primaryKey = PrimaryKey(fleetId, eventId)
}
