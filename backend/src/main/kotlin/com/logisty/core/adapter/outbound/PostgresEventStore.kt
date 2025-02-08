package com.logisty.core.adapter.outbound

import com.logisty.core.application.persistence.event.Events
import com.logisty.core.domain.model.event.InternalEvent
import com.logisty.core.domain.model.values.FleetId
import com.logisty.core.domain.port.EventStore
import org.jetbrains.exposed.sql.andWhere
import org.jetbrains.exposed.sql.insert
import org.jetbrains.exposed.sql.selectAll
import org.springframework.stereotype.Repository
import java.time.Instant

@Repository
class PostgresEventStore : EventStore {
    override fun append(event: InternalEvent) {
        Events.insert {
            it[Events.fleetId] = event.fleetId.value
            it[Events.eventId] = event.eventId.value
            it[Events.type] = event.type.name
            it[Events.appendedAt] = event.appendedAt
            it[Events.payload] = event
        }
    }

    override fun findSince(
        fleetId: FleetId,
        timestamp: Instant,
    ): List<InternalEvent> =
        Events
            .selectAll()
            .where { Events.fleetId eq fleetId.value }
            .andWhere { Events.appendedAt greater timestamp }
            .map { it[Events.payload] }
}
