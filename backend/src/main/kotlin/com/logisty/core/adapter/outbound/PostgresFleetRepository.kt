package com.logisty.core.adapter.outbound

import com.logisty.core.application.persistence.tables.Fleets
import com.logisty.core.domain.model.Fleet
import com.logisty.core.domain.model.values.FleetId
import com.logisty.core.domain.model.values.FleetName
import com.logisty.core.domain.port.FleetRepository
import org.jetbrains.exposed.sql.ResultRow
import org.jetbrains.exposed.sql.insert
import org.jetbrains.exposed.sql.selectAll
import org.springframework.stereotype.Repository

@Repository
class PostgresFleetRepository : FleetRepository {
    override fun findById(fleetId: FleetId): Fleet? =
        Fleets
            .selectAll()
            .where { Fleets.fleetId eq fleetId.value }
            .singleOrNull()
            ?.toFleet()

    override fun findByName(fleetName: FleetName): Fleet? =
        Fleets
            .selectAll()
            .where { Fleets.fleetName eq fleetName.value }
            .singleOrNull()
            ?.toFleet()

    override fun createFleet(fleetName: FleetName): FleetId {
        val fleetId = FleetId.generate()

        Fleets.insert {
            it[Fleets.fleetId] = fleetId.value
            it[Fleets.fleetName] = fleetName.value
        }

        return fleetId
    }
}

private fun ResultRow.toFleet(): Fleet =
    Fleet(
        fleetId = FleetId(this[Fleets.fleetId]),
        fleetName = FleetName(this[Fleets.fleetName]),
    )
