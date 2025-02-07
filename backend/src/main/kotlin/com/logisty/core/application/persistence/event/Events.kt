package com.logisty.core.application.persistence.event

import com.fasterxml.jackson.databind.JsonNode
import com.fasterxml.jackson.module.kotlin.jacksonObjectMapper
import org.jetbrains.exposed.sql.Table
import org.jetbrains.exposed.sql.javatime.timestamp
import org.jetbrains.exposed.sql.json.jsonb

object Events : Table() {
    private val mapper = jacksonObjectMapper()

    val fleetId = uuid("fleet_id")
    val eventId = uuid("event_id")
    val type = varchar("type", 255)
    val appendedAt = timestamp("appended_at")
    val payload =
        jsonb<JsonNode>(
            "event_payload",
            serialize = { mapper.writeValueAsString(it) },
            deserialize = { mapper.readTree(it) },
        )

    override val primaryKey = PrimaryKey(fleetId, eventId)
}
